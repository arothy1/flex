package com.kakaopay.flex.api.roomuser.repository;

import com.kakaopay.flex.api.roomuser.entity.RoomUser;
import com.kakaopay.flex.api.sprinkle.vo.RequestSprinkle;
import com.kakaopay.flex.api.sprinkledistribute.vo.SprinkleDistributeQueryVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomUserRepository extends JpaRepository<RoomUser, Long> {

    List<RoomUser> findByRoomId(String roomId);

    boolean existsByRoomIdAndUserId(SprinkleDistributeQueryVo vo);
}