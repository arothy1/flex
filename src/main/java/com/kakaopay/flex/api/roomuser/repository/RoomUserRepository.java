package com.kakaopay.flex.api.roomuser.repository;

import com.kakaopay.flex.api.roomuser.entity.RoomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomUserRepository extends JpaRepository<RoomUser, Long> {

    List<RoomUser> findByRoomId(String roomId);

}