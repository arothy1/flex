package com.kakaopay.flex.api.action.entity;

import com.kakaopay.flex.api.user.entity.User;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Entity(name = "sprinkle")
public class Action implements Serializable {

	@Id
	private String token;

	private long money;
	private Date registerDate;

	private long senderId;
	private long receiverId;

	@Transient
	private List<Long> receiverIdList;

}