package com.kakaopay.flex.api.sprinkledistribute.service;

import com.kakaopay.flex.api.sprinkledistribute.entity.SprinkleDistribute;
import com.kakaopay.flex.api.sprinkledistribute.repository.SprinkleDistributeRepository;
import org.springframework.stereotype.Service;

@Service
public class SprinkleDistributeServiceImpl implements SprinkleDistributeService {

	public SprinkleDistributeServiceImpl(SprinkleDistributeRepository sprinkleDistributeRepository) {
		this.sprinkleDistributeRepository = sprinkleDistributeRepository;
	}

	SprinkleDistributeRepository sprinkleDistributeRepository;

	@Override
	public void saveSprinkleDistribute(SprinkleDistribute sprinkleDistribute) {
		sprinkleDistributeRepository.save(sprinkleDistribute);
	}
}