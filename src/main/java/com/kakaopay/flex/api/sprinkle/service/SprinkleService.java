package com.kakaopay.flex.api.sprinkle.service;

import com.kakaopay.flex.api.pick.entity.Pick;
import com.kakaopay.flex.api.pick.repository.PickRepository;
import com.kakaopay.flex.api.sprinkle.entity.Sprinkle;
import com.kakaopay.flex.api.sprinkle.repository.SprinkleRepository;
import com.kakaopay.flex.api.sprinkle.vo.RequestSprinkle;
import com.kakaopay.flex.api.sprinkle.vo.ResponseSprinkle;
import com.kakaopay.flex.exception.InvalidRequestException;
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

	public SprinkleService(SprinkleRepository sprinkleRepository, PickRepository pickRepository) {
		this.sprinkleRepository = sprinkleRepository;
		this.pickRepository = pickRepository;
	}

	private SprinkleRepository sprinkleRepository;
	private PickRepository pickRepository;

	private String token;
	private long xUserId;
	private String xRoomId;

	@Transactional
	public String doSprinkle(RequestSprinkle requestSprinkle) {
		this.token = this.getGeneratedToken();
		this.xUserId = requestSprinkle.getXUserId();
		this.xRoomId = requestSprinkle.getXRoomId();

		// 1. 뿌리기를 하자
		this.insertSprinkle(requestSprinkle);
		// 2. 뿌리기 세팅을 하자
		this.insertListPick(requestSprinkle);

		return token;
	}

	@Transactional
	public Integer doReceive(RequestSprinkle requestSprinkle) {
		this.token = requestSprinkle.getToken();
		this.xUserId = requestSprinkle.getXUserId();
		this.xRoomId = requestSprinkle.getXRoomId();

		int pickedMoney;

		Optional<Sprinkle> maybeSprinkle = sprinkleRepository.findByToken(this.token);
		if (maybeSprinkle.isPresent()) {
			Sprinkle sprinkle = maybeSprinkle.get();
			if (sprinkle.getSendTime().isBefore(LocalDateTime.now().minusMinutes(10))) {//FIXME TEST
				throw new InvalidRequestException("종료된 뿌리기에요");
			}
		}

		// 토큰에 해당하는 픽 목록을 가져온다.
		List<Pick> pickList = pickRepository.findByToken(this.token);

		// 이미 받은 유저인지 체크한다.
		if (getIsAlreadyPicked(pickList)) {
			throw new InvalidRequestException("이미 받았어요");
		}
		// 받을 픽이 있는지 체크
		// 방 번호가 같은지 체크
		Optional<Pick> maybePick = pickList.stream()
				.filter(pick -> {
					if (StringUtils.isEmpty(pick.getReceiveUserId()) &&
							pick.getRoomId().equals(this.xRoomId)){
						return true;
					} else {
						return false;
					}
				})
				.findFirst();

		if (maybePick.isPresent()) {
			Pick pick = maybePick.get();

			if (pick.getSendUserId() == this.xUserId) {
				throw new InvalidRequestException("내가 뿌린 돈은 내가 가져갈 수 없어요");
			}

			pick.setReceiveUserId(this.xUserId);
			pickRepository.save(pick);
			pickedMoney = pick.getMoney();
		} else {
			throw new InvalidRequestException("주을돈이 없어요");
		}

		return pickedMoney;
	}

	public ResponseSprinkle getSprinkle(RequestSprinkle requestSprinkle) {
		this.token = requestSprinkle.getToken();
		this.xUserId = requestSprinkle.getXUserId();
		this.xRoomId = requestSprinkle.getXRoomId();

		Optional<Sprinkle> maybeSprinkle = this.sprinkleRepository.findByTokenAndSendUserId(this.token, this.xUserId);
		if (maybeSprinkle.isPresent()) {
			Sprinkle sprinkle = maybeSprinkle.get();

			if (sprinkle.getSendTime().isBefore(LocalDateTime.now().minusDays(7))) {
				throw new InvalidRequestException("조회기간이 지났어요");
			}

			LocalDateTime sendTime = sprinkle.getSendTime();
			int sendMoney = sprinkle.getMoney();

			List<Pick> pickList = this.pickRepository.findByToken(this.token);
			int totalReceiveMoney = pickList.stream()
					.filter(pick -> pick.getReceiveUserId() != null)
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

			ResponseSprinkle responseSprinkle = ResponseSprinkle.builder()
					.sprinkleTime(sendTime)
					.sprinkleMoney(sendMoney)
					.finishTotalReceiveMoney(totalReceiveMoney)
					.finishReceiveInfoList(finishReceiveInfoList)
					.build();

			return responseSprinkle;
		} else {
			throw new InvalidRequestException("조회할 뿌리기가 없어요");
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
						.sprinkleDate(LocalDateTime.now())
						.build())
				.collect(Collectors.toList());
		pickRepository.saveAll(pickList);
	}

	private boolean getIsAlreadyPicked(List<Pick> pickList) {
		boolean isAlreadyPicked = pickList.stream()
				.anyMatch(pick -> pick.getReceiveUserId() != null && pick.getReceiveUserId() == this.xUserId);
		return isAlreadyPicked;
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
			// 사용가능한 키를 찾다가는 무한루프가 될 가능성이 존재하여 단일 생성 후 사용자 재요청 처리함.
			throw new DuplicateKeyException("이미 존재하는 토큰이 생성되었어요. 다시 시도하면 신규 토큰을 발급해게요.");
		}

		return token;
	}

	private List<Integer> getDivisionMoneyList(int money, int divisionCount) {
		int lessMoney = money;

		Random rn = new Random();
		List<Integer> resultList = new ArrayList<>();

		for (int i = 0; i < divisionCount - 1; i++) {
			int randomMoney = rn.nextInt(lessMoney - divisionCount - i);
			lessMoney -= randomMoney;
			resultList.add(randomMoney);
		}
		resultList.add(lessMoney);
		return resultList;
	}

}