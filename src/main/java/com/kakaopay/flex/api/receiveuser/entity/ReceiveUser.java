package com.kakaopay.flex.api.receiveuser.entity;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiveUser implements Serializable {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String token;
	private long userId;
	private long money;


}