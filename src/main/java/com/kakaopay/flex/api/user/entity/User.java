package com.kakaopay.flex.api.user.entity;

import com.kakaopay.flex.api.sprinkle.entity.Sprinkle;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Entity(name = "user")
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	String SprinkleToken;

	@Transient
	private List<String> SprinkleTokenList;

}