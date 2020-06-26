package com.kakaopay.flex.api.sprinkledistribute.vo;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SprinkleDistributeQueryVo implements Serializable {

	private String roomId;
	private long userId;

}