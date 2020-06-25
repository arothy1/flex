package com.kakaopay.flex.api.sprinkle.repository;

import com.kakaopay.flex.api.sprinkle.entity.Sprinkle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprinkleRepository extends JpaRepository<Sprinkle, Long> {

}