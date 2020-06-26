package com.kakaopay.flex.api.roomuser.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomUser implements Serializable {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String roomId;
	private long userId;

}