package com.kakaopay.flex.api.sprinkle.repository;

import com.kakaopay.flex.api.sprinkle.entity.Sprinkle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SprinkleRepository extends JpaRepository<Sprinkle, Long> {

    boolean existsByToken(String token);

    Optional<Sprinkle> findByToken(String token);
}