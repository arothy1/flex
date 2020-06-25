package com.kakaopay.flex.api.sprinkle.entity;

import com.kakaopay.flex.api.user.entity.User;
import java.io.Serializable;
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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Entity(name = "sprinkle")
public class Sprinkle implements Serializable {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="sprinkleId")
	private long id;
	private long money;
	private Date registerDate;

	private long senderId;
	@Transient
	List<User> receiverList;

}