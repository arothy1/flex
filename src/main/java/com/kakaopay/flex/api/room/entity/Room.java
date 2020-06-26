package com.kakaopay.flex.api.room.entity;

import com.kakaopay.flex.api.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Room implements Serializable {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String roomId;

}