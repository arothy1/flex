package com.kakaopay.flex.api.sprinkle.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sprinkle implements Serializable {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String token;
	private int receiveTargetCount;
	private int money;
	private long sendUserId;
	private LocalDateTime sendTime;

}