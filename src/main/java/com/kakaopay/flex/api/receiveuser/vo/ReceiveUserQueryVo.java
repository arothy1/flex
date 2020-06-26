package com.kakaopay.flex.api.receiveuser.vo;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiveUserQueryVo implements Serializable {

	private String token;
	private long userId;

}