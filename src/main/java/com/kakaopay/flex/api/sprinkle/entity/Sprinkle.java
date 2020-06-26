package com.kakaopay.flex.api.sprinkle.entity;

import com.kakaopay.flex.api.receiveuser.entity.ReceiveUser;
import com.kakaopay.flex.api.room.entity.Room;
import com.kakaopay.flex.api.user.entity.User;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import lombok.*;

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

	// FIXME column length 3
	private String token;

	@Transient
	private Room room;

	private Long sendUserId;
	private LocalDateTime sendTime;
	private Long sendMoney;

	private Long totalReceiveMoney;

	@Transient
	private List<Long> distributedMoneyList;

	@Transient
	private List<ReceiveUser> receiveUserList;

}