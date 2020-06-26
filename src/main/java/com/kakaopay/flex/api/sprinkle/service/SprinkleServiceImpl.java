package com.kakaopay.flex.api.sprinkle.service;

import com.kakaopay.flex.api.receiveuser.entity.ReceiveUser;
import com.kakaopay.flex.api.receiveuser.repository.ReceiveUserRepository;
import com.kakaopay.flex.api.receiveuser.vo.ReceiveUserQueryVo;
import com.kakaopay.flex.api.room.entity.Room;
import com.kakaopay.flex.api.roomuser.entity.RoomUser;
import com.kakaopay.flex.api.roomuser.repository.RoomUserRepository;
import com.kakaopay.flex.api.sprinkle.entity.Sprinkle;
import com.kakaopay.flex.api.sprinkle.repository.SprinkleRepository;
import com.kakaopay.flex.api.sprinkle.vo.RequestSprinkle;
import com.kakaopay.flex.api.sprinkledistribute.entity.SprinkleDistribute;
import com.kakaopay.flex.api.sprinkledistribute.repository.SprinkleDistributeRepository;
import com.kakaopay.flex.api.sprinkledistribute.vo.SprinkleDistributeQueryVo;
import com.kakaopay.flex.api.user.entity.User;
import com.kakaopay.flex.api.user.repository.UserRepository;
import com.sun.jdi.request.InvalidRequestStateException;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import javax.transaction.NotSupportedException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SprinkleServiceImpl implements SprinkleService {

	public SprinkleServiceImpl(SprinkleRepository sprinkleRepository,
							   UserRepository userRepository,
							   SprinkleDistributeRepository sprinkleDistributeRepository,
							   RoomUserRepository roomUserRepository,
							   ReceiveUserRepository receiveUserRepository) {
		this.sprinkleRepository = sprinkleRepository;
		this.userRepository = userRepository;
		this.sprinkleDistributeRepository = sprinkleDistributeRepository;
		this.roomUserRepository = roomUserRepository;
		this.receiveUserRepository = receiveUserRepository;
	}

	SprinkleRepository sprinkleRepository;
	UserRepository userRepository;
	SprinkleDistributeRepository sprinkleDistributeRepository;
	RoomUserRepository roomUserRepository;
	ReceiveUserRepository receiveUserRepository;

	String token;
	long xUserId;

	@Override
	@Transactional
	public String doSprinkle(RequestSprinkle requestSprinkle) throws Exception {
		this.token = this.getGeneratedToken();
		this.xUserId = requestSprinkle.getXUserId();
		Optional<User> maybeSendUser = userRepository.findById(xUserId);
		maybeSendUser.ifPresent(sendUser -> {
			this.insertSprinkle(requestSprinkle);
			this.updateSendUserBudget(sendUser, requestSprinkle);
			this.insertListSprinkledDistribute(requestSprinkle);
		});

		return token;
	}

	@Override
	public Object doReceive(RequestSprinkle requestSprinkle) throws Exception {
		this.token = requestSprinkle.getToken();
		this.xUserId = requestSprinkle.getXUserId();

		Optional<Sprinkle> maybeSprinkle = sprinkleRepository.findByToken(token);
		maybeSprinkle.ifPresent(sprinkle -> {
			// 1. 보낸 사용자인지 체크
			if (sprinkle.getSendUserId() == xUserId) {
				throw new InvalidRequestStateException("invalid accept user");
			}

			// 2. 받기 시간이 남았는지 체크
			if (sprinkle.getSendTime().isBefore(LocalDateTime.now().minusMinutes(10))) {
				throw new InvalidRequestStateException("sprinkle money was expired");
			}

			// 3. 받을 돈이 남았는지 체크
			List<SprinkleDistribute> sprinkleDistributeList = sprinkleDistributeRepository.findByToken(token);
			sprinkleDistributeList.stream()
					.filter(sprinkleDistribute -> sprinkleDistribute.getReceiveUserId() != xUserId)
					.findFirst()
					.ifPresentOrElse(sprinkleDistribute -> {

						// 4. 방에 속해있는지 체크
						String roomId = sprinkle.getRoom().getRoomId();
						SprinkleDistributeQueryVo sprinkleDistributeQueryVo = SprinkleDistributeQueryVo.builder()
								.roomId(roomId)
								.userId(xUserId)
								.build();
						boolean isRoomUser = roomUserRepository.existsByRoomIdAndUserId(sprinkleDistributeQueryVo);
						if (!isRoomUser) {
							throw new InvalidRequestStateException("access denied");
						}

						// 5. 이미 받은 적 있는지 체크
						ReceiveUserQueryVo receiveUserQueryVo = ReceiveUserQueryVo.builder()
								.token(token)
								.userId(xUserId)
								.build();

						boolean isExistReceiveRecord = receiveUserRepository.existsByTokenAndUserId(receiveUserQueryVo);
						if (isExistReceiveRecord) {
							throw new InvalidRequestStateException("no more pick up");
						}

						Optional<User> maybeReceiveUser = userRepository.findById(xUserId);
						maybeReceiveUser.ifPresentOrElse(user -> {
							long receiveMoney = sprinkleDistribute.getDistributedMoney();
							long beforeMoney = user.getBudget();
							long afterMoney = beforeMoney + receiveMoney;
							user.setBudget(afterMoney);
							// 1. 사용자 돈 추가
							userRepository.save(user);

							// 2. receiveUser 받은내용 추가
							ReceiveUser receiveUser = ReceiveUser.builder()
									.userId(xUserId)
									.token(token)
									.money(receiveMoney)
									.build();
							receiveUserRepository.save(receiveUser);
							// 3. distribute 돈 감소소


							
//							sprinkleDistribute.
//							sprinkleDistributeRepository.save(sprinkleDistribute);

						}, ()-> {
							throw new InvalidRequestStateException("");
						});

					}, () -> {
						throw new InvalidRequestStateException("sprinkle money was exhausted");
					});

		});

		return null;
	}

//	private boolean getIsAbleReceive(RequestSprinkle requestSprinkle) {
//		boolean isAbleReceive = true;
//
//		try {
//			Optional<Sprinkle> maybeSprinkle = sprinkleRepository.findByToken(token);
//			maybeSprinkle.ifPresent(sprinkle -> {
//				// 1. 보낸 사용자인지 체크
//				if (sprinkle.getSendUserId() == xUserId) {
//					throw new InvalidRequestStateException("invalid accept user");
//	//				isSendUser = true;
//				}
//
//				// 2. 받기 시간이 남았는지 체크
//				if (sprinkle.getSendTime().isBefore(LocalDateTime.now().minusMinutes(10))) {
//					throw new InvalidRequestStateException("sprinkle money was expired");
//	//				isExpireMoney = true;
//				}
//
//				// 3. 받을 돈이 남았는지 체크
//				List<SprinkleDistribute> sprinkleDistributeList = sprinkleDistributeRepository.findByToken(token);
//				sprinkleDistributeList.stream()
//						.filter(sprinkleDistribute -> sprinkleDistribute.getReceiveUserId() != xUserId)
//						.findFirst()
//						.ifPresentOrElse(sprinkleDistribute -> {
//	//						isExistLessMoney = true;
//
//							// 4. 방에 속해있는지 체크
//							String roomId = sprinkle.getRoom().getRoomId();
//							SprinkleDistributeQueryVo sprinkleDistributeQueryVo = SprinkleDistributeQueryVo.builder()
//									.roomId(roomId)
//									.userId(xUserId)
//									.build();
//							boolean isRoomUser = roomUserRepository.existsByRoomIdAndUserId(sprinkleDistributeQueryVo);
//							if (!isRoomUser) {
//								throw new InvalidRequestStateException("access denied");
//							}
//
//							// 5. 이미 받은 적 있는지 체크
//							ReceiveUserQueryVo receiveUserQueryVo = ReceiveUserQueryVo.builder()
//									.token(token)
//									.userId(xUserId)
//									.build();
//
//							boolean isExistReceiveRecord = receiveUserRepository.existsByTokenAndUserId(receiveUserQueryVo);
//							if (isExistReceiveRecord) {
//								throw new InvalidRequestStateException("no more pick up");
//							}
//
//							Optional<User> maybeReceiveUser = userRepository.findById(xUserId);
//							maybeReceiveUser.ifPresentOrElse(user -> {
//								long receiveMoney = sprinkleDistribute.getDistributedMoney();
//								long beforeMoney = user.getBudget();
//								long afterMoney = beforeMoney + receiveMoney;
//								user.setBudget(afterMoney);
//								userRepository.save(user);
//							}, () -> {
//								throw new InvalidRequestStateException("");
//							});
//
//						}, () -> {
//							throw new InvalidRequestStateException("sprinkle money was exhausted");
//						});
//
//			});
//		} catch (Exception e) {
//			log.error("{}", e);
//			isAbleReceive = false;
//		}
//
//		return isAbleReceive;
//	}

	private boolean getIsRoomUser(RequestSprinkle requestSprinkle) {
		return false;
	}

	private boolean getIsExpireMoney(RequestSprinkle requestSprinkle) {
		return false;
	}

	private boolean getIsExistLessMoney(RequestSprinkle requestSprinkle) {
		return false;
	}

	private boolean getIsSendUserAndIsNotExpireMoney(RequestSprinkle requestSprinkle) {
		boolean isSendUser = false;
		Optional<Sprinkle> maybeSprinkle = sprinkleRepository.findByToken(token);
		if (maybeSprinkle.isPresent()) {
			if (maybeSprinkle.get().getSendUserId() == xUserId) {
				isSendUser = true;
			}
		}
		return isSendUser;
	}

	private boolean getValidReceiveUser(SprinkleDistribute sprinkleDistribute) {

		return false;
	}

	private boolean getValidTokenExpire(Sprinkle sprinkle) {
		LocalDateTime sprinkleDateTime = sprinkle.getSendTime();
		LocalDateTime now = LocalDateTime.now();
		sprinkleDateTime.minusMinutes(10);
		return sprinkleDateTime.isAfter(now);
	}

	private void insertSprinkle(RequestSprinkle requestSprinkle) {
		long xUserId = requestSprinkle.getXUserId();
		String xRoomId = requestSprinkle.getXRoomId();
		long sprinkleMoney = requestSprinkle.getSprinkleMoney();

		Sprinkle sprinkle = Sprinkle.builder()
				.token(token)
				.room(Room.builder().roomId(xRoomId).build())
				.sendUserId(xUserId)
				.sendTime(LocalDateTime.now())
				.sendMoney(sprinkleMoney)
				.build();
		this.sprinkleRepository.save(sprinkle);
	}

	private void updateSendUserBudget(User sendUser, RequestSprinkle requestSprinkle) {
		long originalBudget = sendUser.getBudget();
		long lossBudget = requestSprinkle.getSprinkleMoney();
		long resultBudget = originalBudget - lossBudget;
		sendUser.setBudget(resultBudget);
		this.userRepository.save(sendUser);
	}

	private void insertListSprinkledDistribute(RequestSprinkle requestSprinkle) {
		long sprinkleMoney = requestSprinkle.getSprinkleMoney();
		int receiveUserCount = requestSprinkle.getReceiveUserCount();

		List<Long> distributedMoneyList = getDistributedMoneyList(sprinkleMoney, receiveUserCount);
		List<SprinkleDistribute> sprinkleDistributeList = distributedMoneyList.stream()
				.map(distributedMoney -> SprinkleDistribute.builder()
						.token(token)
						.distributedMoney(distributedMoney)
						.build())
				.collect(Collectors.toList());
		sprinkleDistributeRepository.saveAll(sprinkleDistributeList);
	}

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

	private List<Long> getDistributedMoneyList(Long money, int distributeCount) {
		long lessMoney = money;
		List<Long> resultList = new ArrayList<>();

		for (int i = 0; i < distributeCount - 1; i++) {
			lessMoney -= lessMoney / distributeCount;
			resultList.add(lessMoney);
		}
		resultList.add(lessMoney);
		return resultList;
	}
}