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
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
public class Pick implements Serializable {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String token;
	private String roomId;
	private long sendUserId;
	private Long receiveUserId;
	private int money;
	private LocalDateTime sprinkleDate;

}