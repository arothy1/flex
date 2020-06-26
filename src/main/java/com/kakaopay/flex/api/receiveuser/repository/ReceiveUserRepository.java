package com.kakaopay.flex.api.receiveuser.repository;

import com.kakaopay.flex.api.receiveuser.entity.ReceiveUser;
import com.kakaopay.flex.api.receiveuser.vo.ReceiveUserQueryVo;
import com.kakaopay.flex.api.roomuser.entity.RoomUser;
import com.kakaopay.flex.api.sprinkledistribute.vo.SprinkleDistributeQueryVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiveUserRepository extends JpaRepository<ReceiveUser, Long> {

    Optional<ReceiveUser> findByTokenAndUserId(ReceiveUserQueryVo receiveUserQueryVo);
    boolean existsByTokenAndUserId(ReceiveUserQueryVo receiveUserQueryVo);
}