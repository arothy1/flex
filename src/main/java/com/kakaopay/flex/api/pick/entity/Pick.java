package com.kakaopay.flex.api.pick.entity;

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
public class Pick implements Serializable {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String token;
	private String roomId;
	private long sendUserId;
	private long receiveUserId;
	private int money;
	private LocalDateTime sprinkleDate;

}