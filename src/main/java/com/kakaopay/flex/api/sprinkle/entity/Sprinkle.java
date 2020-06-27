package com.kakaopay.flex.api.sprinkle.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
public class Sprinkle implements Serializable {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String token;
	private int receiveTargetCount;
	private int money;
	private long sendUserId;
	private LocalDateTime sendTime;

}