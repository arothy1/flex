package com.kakaopay.flex.api.sprinkle.entity;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Sprinkle implements Serializable {

	String token;

	String roomCode;

	long sendUser;

	LocalDateTime sendTime;

	int sendMoney;

	int receiveUserCount;

	int totalFinishReceiveMoney;

	List<ReceiveInfo> receivedInfoList;
}