package com.kakaopay.flex.api.sprinkledistribute.repository;

import com.kakaopay.flex.api.sprinkledistribute.entity.SprinkleDistribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SprinkleDistributeRepository extends JpaRepository<SprinkleDistribute, Long> {

    Optional<SprinkleDistribute> findByTokenAndReceiveUserId(SprinkleDistribute sprinkleDistribute);

    List<SprinkleDistribute> findByToken(String token);
}