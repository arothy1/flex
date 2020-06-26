package com.kakaopay.flex.api.pick.repository;

import com.kakaopay.flex.api.pick.entity.Pick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PickRepository extends JpaRepository<Pick, Long> {

    List<Pick> findByToken(String token);

}