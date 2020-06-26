package com.kakaopay.flex.api.sprinkle.vo;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestSprinkle implements Serializable {

	private long xUserId;
	private String xRoomId;
	private int sprinkleMoney;
	private int receiveUserCount;
	private String token;

}