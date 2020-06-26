package com.kakaopay.flex.api.sprinkle.vo;

import com.kakaopay.flex.api.receiveuser.entity.ReceiveUser;
import com.kakaopay.flex.api.room.entity.Room;
import com.kakaopay.flex.api.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RequestSprinkle implements Serializable {

	private long xUserId;
	private String xRoomId;
	private long sprinkleMoney;
	private int receiveUserCount;
	private String token;

}