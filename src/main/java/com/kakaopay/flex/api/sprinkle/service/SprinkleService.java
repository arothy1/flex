package com.kakaopay.flex.api.sprinkle.service;

import com.kakaopay.flex.api.pick.entity.Pick;
import com.kakaopay.flex.api.pick.repository.PickRepository;
import com.kakaopay.flex.api.roomuser.repository.RoomUserRepository;
import com.kakaopay.flex.api.sprinkle.entity.Sprinkle;
import com.kakaopay.flex.api.sprinkle.repository.SprinkleRepository;
import com.kakaopay.flex.api.sprinkle.vo.RequestSprinkle;
import com.kakaopay.flex.api.sprinkle.vo.ResponseSprinkle;
import com.sun.jdi.request.InvalidRequestStateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SprinkleService {

	public SprinkleService(SprinkleRepository sprinkleRepository,
						   RoomUserRepository roomUserRepository,
						   PickRepository pickRepository) {
		this.sprinkleRepository = sprinkleRepository;
		this.pickRepository = pickRepository;
		this.roomUserRepository = roomUserRepository;
	}

	SprinkleRepository sprinkleRepository;
	PickRepository pickRepository;
	RoomUserRepository roomUserRepository;

	String token;
	long xUserId;
	String xRoomId;

	@Transactional
	public String doSprinkle(RequestSprinkle requestSprinkle) throws Exception {
		this.token = this.getGeneratedToken();
		this.xUserId = requestSprinkle.getXUserId();
		this.xRoomId = requestSprinkle.getXRoomId();

		// 1. 뿌리기를 하자
		this.insertSprinkle(requestSprinkle);
		// 2. 뿌리기 세팅을 하자
		this.insertListPick(requestSprinkle);
		// 3. 잔액 차감 - 요구 사항에 없다.

		return token;
	}



	public Integer doReceive(RequestSprinkle requestSprinkle) throws Exception {
		this.token = requestSprinkle.getToken();
		this.xUserId = requestSprinkle.getXUserId();
		this.xRoomId = requestSprinkle.getXRoomId();

		int pickedMoney = 0;

		// 1. 토큰에 해당하는 픽 목록을 가져온다.
		List<Pick> pickList = pickRepository.findByToken(this.token);

		// 2. 이미 받은 유저인지 체크한다.
		boolean isAlreadyPicked = pickList.stream()
				.anyMatch(pick -> pick.getReceiveUserId() == this.xUserId);
		if (isAlreadyPicked) {
			return 0;//FIXME throw
		}
		// 뿌린 사람인지 체크
		// 10분 체크
		// 받을 픽이 있는지 체크
		// 방 번호가 같은지 체크
		Optional<Pick> maybyPick = pickList.stream()
				.filter(pick -> {
					if (pick.getSprinkleDate().isBefore(LocalDateTime.now().minusMinutes(10)) &&
							StringUtils.isEmpty(pick.getReceiveUserId()) &&
							pick.getSendUserId() != this.xUserId &&
							pick.getRoomId().equals(this.xRoomId)){
						return true;
					} else {
						return false;
					}
				})
				.findFirst();

		if (maybyPick.isPresent()) {
			Pick pick = maybyPick.get();
			pick.setReceiveUserId(this.xUserId);
			pickRepository.save(pick);
			pickedMoney = pick.getMoney();
		}

		return pickedMoney;
	}

	public Object getSprinkle(RequestSprinkle requestSprinkle) {
		this.token = requestSprinkle.getToken();
		this.xUserId = requestSprinkle.getXUserId();
		this.xRoomId = requestSprinkle.getXRoomId();

		Optional<Sprinkle> maybeSprinkle = this.sprinkleRepository.findByTokenAndSendUserId(this.token, this.xUserId);
		if (maybeSprinkle.isPresent()) {
			Sprinkle sprinkle = maybeSprinkle.get();

			if (sprinkle.getSendTime().isBefore(LocalDateTime.now().minusDays(7))) {
				throw new InvalidRequestStateException("access denied");
			}

			LocalDateTime sendTime = sprinkle.getSendTime();
			int sendMoney = sprinkle.getMoney();

			List<Pick> pickList = this.pickRepository.findByToken(this.token);
			int totalReceiveMoney = pickList.stream()
					.collect(Collectors.summingInt(value -> value.getMoney()));

			List<Map<String, Long>> finishReceiveInfoList = pickList.stream()
					.filter(pick -> pick.getReceiveUserId() != null)
					.map(pick -> {
						Map<String, Long> info = new HashMap();
						info.put("receiveMoney", Long.valueOf(pick.getMoney()));
						info.put("receiveUserId", pick.getReceiveUserId());
						return info;
					})
					.collect(Collectors.toList());

			ResponseSprinkle responseSprikle = ResponseSprinkle.builder()
					.sprinkleTime(sendTime)
					.sprinkleMoney(sendMoney)
					.finishTotalReceiveMoney(totalReceiveMoney)
					.finishReceiveInfoList(finishReceiveInfoList)
					.build();

			return responseSprikle;
		} else {
			throw new InvalidRequestStateException("access denied");
		}
	}


	private void insertSprinkle(RequestSprinkle requestSprinkle) {
		int sprinkleMoney = requestSprinkle.getSprinkleMoney();

		Sprinkle sprinkle = Sprinkle.builder()
				.token(this.token)
				.receiveTargetCount(requestSprinkle.getReceiveUserCount())
				.sendUserId(this.xUserId)
				.sendTime(LocalDateTime.now())
				.money(sprinkleMoney)
				.build();
		this.sprinkleRepository.save(sprinkle);
	}

	private void insertListPick(RequestSprinkle requestSprinkle) {
		int sprinkleMoney = requestSprinkle.getSprinkleMoney();
		int receiveUserCount = requestSprinkle.getReceiveUserCount();

		List<Integer> divisionMoneyList = getDivisionMoneyList(sprinkleMoney, receiveUserCount);
		List<Pick> pickList = divisionMoneyList.stream()
				.map(divisionMoney -> Pick.builder()
						.token(this.token)
						.roomId(this.xRoomId)
						.sendUserId(this.xUserId)
						.money(divisionMoney)
						.build())
				.collect(Collectors.toList());
		pickRepository.saveAll(pickList);
	}


//	public Object doReceive(RequestSprinkle requestSprinkle) throws Exception {
//		this.token = requestSprinkle.getToken();
//		this.xUserId = requestSprinkle.getXUserId();
//
//		Optional<Sprinkle> maybeSprinkle = sprinkleRepository.findByToken(token);
//		maybeSprinkle.ifPresent(sprinkle -> {
//			// 1. 보낸 사용자인지 체크
//			if (sprinkle.getSendUserId() == xUserId) {
//				throw new InvalidRequestStateException("invalid accept user");
//			}
//
//			// 2. 받기 시간이 남았는지 체크
//			if (sprinkle.getSendTime().isBefore(LocalDateTime.now().minusMinutes(10))) {
//				throw new InvalidRequestStateException("sprinkle money was expired");
//			}
//
//			// 3. 받을 돈이 남았는지 체크
//			List<SprinkleDistribute> sprinkleDistributeList = sprinkleDistributeRepository.findByToken(token);
//			sprinkleDistributeList.stream()
//					.filter(sprinkleDistribute -> sprinkleDistribute.getReceiveUserId() != xUserId)
//					.findFirst()
//					.ifPresentOrElse(sprinkleDistribute -> {
//
//						// 4. 방에 속해있는지 체크
//						String roomId = sprinkle.getRoom().getRoomId();
//						SprinkleDistributeQueryVo sprinkleDistributeQueryVo = SprinkleDistributeQueryVo.builder()
//								.roomId(roomId)
//								.userId(xUserId)
//								.build();
//						boolean isRoomUser = roomUserRepository.existsByRoomIdAndUserId(sprinkleDistributeQueryVo);
//						if (!isRoomUser) {
//							throw new InvalidRequestStateException("access denied");
//						}
//
//						// 5. 이미 받은 적 있는지 체크
//						ReceiveUserQueryVo receiveUserQueryVo = ReceiveUserQueryVo.builder()
//								.token(token)
//								.userId(xUserId)
//								.build();
//
//						boolean isExistReceiveRecord = receiveUserRepository.existsByTokenAndUserId(receiveUserQueryVo);
//						if (isExistReceiveRecord) {
//							throw new InvalidRequestStateException("no more pick up");
//						}
//
//						Optional<User> maybeReceiveUser = userRepository.findById(xUserId);
//						maybeReceiveUser.ifPresentOrElse(user -> {
//							long receiveMoney = sprinkleDistribute.getDistributedMoney();
//							long beforeMoney = user.getBudget();
//							long afterMoney = beforeMoney + receiveMoney;
//							user.setBudget(afterMoney);
//							// 1. 사용자 돈 추가
//							userRepository.save(user);
//
//							// 2. receiveUser 받은내용 추가
//							ReceiveUser receiveUser = ReceiveUser.builder()
//									.userId(xUserId)
//									.token(token)
//									.money(receiveMoney)
//									.build();
//							receiveUserRepository.save(receiveUser);
//							// 3. distribute 돈 감소소
//
//
//
////							sprinkleDistribute.
////							sprinkleDistributeRepository.save(sprinkleDistribute);
//
//						}, ()-> {
//							throw new InvalidRequestStateException("");
//						});
//
//					}, () -> {
//						throw new InvalidRequestStateException("sprinkle money was exhausted");
//					});
//
//		});
//
//		return null;
//	}


//	private boolean getIsRoomUser(RequestSprinkle requestSprinkle) {
//		return false;
//	}
//
//	private boolean getIsExpireMoney(RequestSprinkle requestSprinkle) {
//		return false;
//	}
//
//	private boolean getIsExistLessMoney(RequestSprinkle requestSprinkle) {
//		return false;
//	}
//
//	private boolean getIsSendUserAndIsNotExpireMoney(RequestSprinkle requestSprinkle) {
//		boolean isSendUser = false;
//		Optional<Sprinkle> maybeSprinkle = sprinkleRepository.findByToken(token);
//		if (maybeSprinkle.isPresent()) {
//			if (maybeSprinkle.get().getSendUserId() == xUserId) {
//				isSendUser = true;
//			}
//		}
//		return isSendUser;
//	}
//
//	private boolean getValidReceiveUser(SprinkleDistribute sprinkleDistribute) {
//
//		return false;
//	}
//
//	private boolean getValidTokenExpire(Sprinkle sprinkle) {
//		LocalDateTime sprinkleDateTime = sprinkle.getSendTime();
//		LocalDateTime now = LocalDateTime.now();
//		sprinkleDateTime.minusMinutes(10);
//		return sprinkleDateTime.isAfter(now);
//	}
//
//	private void insertSprinkle(RequestSprinkle requestSprinkle) {
//		long xUserId = requestSprinkle.getXUserId();
//		String xRoomId = requestSprinkle.getXRoomId();
//		long sprinkleMoney = requestSprinkle.getSprinkleMoney();
//
//		Sprinkle sprinkle = Sprinkle.builder()
//				.token(token)
//				.room(Room.builder().roomId(xRoomId).build())
//				.sendUserId(xUserId)
//				.sendTime(LocalDateTime.now())
//				.sendMoney(sprinkleMoney)
//				.build();
//		this.sprinkleRepository.save(sprinkle);
//	}
//
//	private void updateSendUserBudget(User sendUser, RequestSprinkle requestSprinkle) {
//		long originalBudget = sendUser.getBudget();
//		long lossBudget = requestSprinkle.getSprinkleMoney();
//		long resultBudget = originalBudget - lossBudget;
//		sendUser.setBudget(resultBudget);
//		this.userRepository.save(sendUser);
//	}
//
//	private void insertListSprinkledDistribute(RequestSprinkle requestSprinkle) {
//		long sprinkleMoney = requestSprinkle.getSprinkleMoney();
//		int receiveUserCount = requestSprinkle.getReceiveUserCount();
//
//		List<Long> distributedMoneyList = getDistributedMoneyList(sprinkleMoney, receiveUserCount);
//		List<SprinkleDistribute> sprinkleDistributeList = distributedMoneyList.stream()
//				.map(distributedMoney -> SprinkleDistribute.builder()
//						.token(token)
//						.distributedMoney(distributedMoney)
//						.build())
//				.collect(Collectors.toList());
//		sprinkleDistributeRepository.saveAll(sprinkleDistributeList);
//	}
//
	private String getGeneratedToken() {
		char[] characters = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9'};
		StringBuffer sb = new StringBuffer();
		Random rn = new Random();

		for( int i = 0 ; i < 3 ; i++ ){
			sb.append( characters[ rn.nextInt( characters.length ) ] );
		}

		String token = sb.toString();
		if (this.sprinkleRepository.existsByToken(token)) {
			throw new DuplicateKeyException("duplicated token for generating. please try again.");
		}

		return token;
	}

	private List<Integer> getDivisionMoneyList(int money, int divisionCount) {
		int lessMoney = money;

		Random rn = new Random();
		List<Integer> resultList = new ArrayList<>();

		for (int i = 0; i < divisionCount - 1; i++) {
			int randomMoney = rn.nextInt(money);
			resultList.add(randomMoney);
		}
		resultList.add(lessMoney);
		return resultList;
	}

}