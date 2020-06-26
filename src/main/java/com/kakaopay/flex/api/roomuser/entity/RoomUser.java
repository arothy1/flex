package com.kakaopay.flex.api.roomuser.entity;

import com.kakaopay.flex.api.receiveuser.entity.ReceiveUser;
import com.kakaopay.flex.api.room.entity.Room;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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