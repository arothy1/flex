package com.kakaopay.flex.api.sprinkle.service;

import com.kakaopay.flex.api.sprinkle.entity.ReceiveInfo;
import com.kakaopay.flex.api.sprinkle.entity.Sprinkle;
import com.kakaopay.flex.api.sprinkle.vo.RequestSprinkle;
import com.kakaopay.flex.api.sprinkle.vo.ResponseSprinkle;
import com.kakaopay.flex.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SprinkleService {

	private final RedisTemplate redisTemplate;

	@Transactional
	public String doSprinkle(RequestSprinkle requestSprinkle) {

		int sprinkleMoney = requestSprinkle.getSprinkleMoney();
		int receiveUserCount = requestSprinkle.getReceiveUserCount();

		if (receiveUserCount < 1) {
			throw new InvalidRequestException("주워갈 인원을 한 명이상 입력해주세요");
		}

		if (sprinkleMoney < receiveUserCount) {
			throw new InvalidRequestException("인원보다 많은 금액을 입력해주세요");
		}

		String token = insertSprinkleAndGetToken(requestSprinkle);

		return token;
	}

	@Transactional
	public Integer doReceive(RequestSprinkle requestSprinkle) {

		String token = requestSprinkle.getToken();
		long xUserId = requestSprinkle.getXUserId();
		String xRoomId = requestSprinkle.getXRoomId();

		ValueOperations<String, Sprinkle> sprinkleOperations = redisTemplate.opsForValue();
		Sprinkle sprinkle = sprinkleOperations.get(token);
		if (sprinkle != null) {
			if (sprinkle.getSendTime().isBefore(LocalDateTime.now().minusMinutes(10))) {
				throw new InvalidRequestException("종료된 뿌리기에요");
			}
		} else {
			throw new InvalidRequestException("정상적인 토큰이 아니에요");
		}

		List<ReceiveInfo> receiveInfoList = sprinkle.getReceivedInfoList();

		// 이미 받은 유저인지 체크한다.
		if (getIsAlreadyPicked(receiveInfoList, xUserId)) {
			throw new InvalidRequestException("이미 받았어요");
		}

		if (!sprinkle.getRoomCode().equals(xRoomId)) {
			throw new InvalidRequestException("방에 속해있지 않아요");
		}

		// 받을 픽이 있는지 체크
		// 방 번호가 같은지 체크
		Optional<ReceiveInfo> maybePick = receiveInfoList.stream()
				.filter(receiveInfo -> StringUtils.isEmpty(receiveInfo.getUserId()))
				.findFirst();

		ReceiveInfo receiveInfo = maybePick.orElseThrow(() -> new InvalidRequestException("받을 돈이 없어요"));

		if (sprinkle.getSendUser() == xUserId) {
			throw new InvalidRequestException("내가 뿌린 돈은 내가 가져갈 수 없어요");
		}

		receiveInfo.setUserId(xUserId);
		for (ReceiveInfo info : receiveInfoList) {
			if (info == receiveInfo) {
				info = receiveInfo;
				break;
			}
		}

		sprinkle.setReceivedInfoList(receiveInfoList);
		sprinkleOperations.set(token, sprinkle);

		return receiveInfo.getMoney();
	}

	private boolean getIsAlreadyPicked(List<ReceiveInfo> receiveInfoList, long xUserId) {
		return receiveInfoList.stream()
				.anyMatch(receiveInfo -> receiveInfo.getUserId() != null && receiveInfo.getUserId() == xUserId);
	}

	public ResponseSprinkle getSprinkle(RequestSprinkle requestSprinkle) {
		String token = requestSprinkle.getToken();
		long xUserId = requestSprinkle.getXUserId();
//		String xRoomId = requestSprinkle.getXRoomId();	// token만 있어도 수행 가능함

		ValueOperations<String, Sprinkle> sprinkleOperations = redisTemplate.opsForValue();

		Sprinkle sprinkle = sprinkleOperations.get(token);

		if (sprinkle != null) {
			if (sprinkle.getSendUser() != xUserId) {
				throw new InvalidRequestException("권한이 없어요");
			}

			if (sprinkle.getSendTime().isBefore(LocalDateTime.now().minusDays(7))) {
				throw new InvalidRequestException("조회기간이 지났어요");
			}

			LocalDateTime sendTime = sprinkle.getSendTime();
			int sendMoney = sprinkle.getSendMoney();
			List<ReceiveInfo> receiveInfoList = sprinkle.getReceivedInfoList();
			List<ReceiveInfo> filteredReceiveInfoList = receiveInfoList.stream()
					.filter(receiveInfo -> receiveInfo.getUserId() != null)
					.collect(Collectors.toList());

			int totalReceiveMoney = filteredReceiveInfoList.stream()
					.mapToInt(receiveInfo -> receiveInfo.getMoney()).sum();

			return ResponseSprinkle.builder()
					.sprinkleTime(sendTime)
					.sprinkleMoney(sendMoney)
					.finishTotalReceiveMoney(totalReceiveMoney)
					.finishReceiveInfoList(filteredReceiveInfoList)
					.build();
		} else {
			throw new InvalidRequestException("조회할 뿌리기가 없어요");
		}
	}


	private String insertSprinkleAndGetToken(RequestSprinkle requestSprinkle) {

		String token = this.getGeneratedToken();

		List<Integer> divisionMoneyList = getDivisionMoneyList(requestSprinkle.getSprinkleMoney(), requestSprinkle.getReceiveUserCount());
		List<ReceiveInfo> receiveInfoList = divisionMoneyList.stream()
				.map(money -> ReceiveInfo.builder()
						.money(money)
						.build())
				.collect(Collectors.toList());

		Sprinkle sprinkle = Sprinkle.builder()
				.token(token)
				.roomCode(requestSprinkle.getXRoomId())
				.sendUser(requestSprinkle.getXUserId())
				.sendTime(LocalDateTime.now())
				.sendMoney(requestSprinkle.getSprinkleMoney())
				.receiveUserCount(requestSprinkle.getReceiveUserCount())
				.totalFinishReceiveMoney(0)
				.receivedInfoList(receiveInfoList).build();

		ValueOperations<String, Sprinkle> sprinkleOperations = redisTemplate.opsForValue();
		sprinkleOperations.set(token, sprinkle);

		return token;
	}

	private String getGeneratedToken() {
		char[] characters = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9'};
		StringBuilder sb = new StringBuilder();
		Random rn = new Random();

		for( int i = 0 ; i < 3 ; i++ ){
			sb.append( characters[ rn.nextInt( characters.length ) ] );
		}

		String token = sb.toString();
		ValueOperations<String, Sprinkle> sprinkleOperations = redisTemplate.opsForValue();
		Sprinkle sprinkle = sprinkleOperations.get(token);
		if (sprinkle != null) {
			// 사용가능한 키를 찾다가는 무한루프가 될 가능성이 존재하여 단일 생성 후 사용자 재요청 처리함.
			throw new DuplicateKeyException("이미 존재하는 토큰이 생성되었어요. 다시 시도하면 신규 토큰을 발급해게요.");
		}

		return token;
	}

	private List<Integer> getDivisionMoneyList(int money, int divisionCount) {
		int lessMoney = money;
		int lessCount = divisionCount;

		Random rn = new Random();
		List<Integer> resultList = new ArrayList<>();

		for (int i = 0; i < divisionCount - 1; i++) {
			--lessCount;
			int randomMoney = rn.nextInt(lessMoney + 1 - lessCount);
			lessMoney -= randomMoney;
			resultList.add(randomMoney);
		}
		resultList.add(lessMoney);
		return resultList;
	}

}