package com.kakaopay.flex.api.sprinkle.vo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSprinkle implements Serializable {

	private LocalDateTime sprinkleTime;
	private int sprinkleMoney;
	private int finishTotalReceiveMoney;
	private List<Map<String, Long>> finishReceiveInfoList;

}