package com.kakaopay.flex.api.sprinkle.service;

import com.kakaopay.flex.api.sprinkle.repository.SprinkleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SprinkleServiceImpl {

	@Autowired
	SprinkleRepository repository;
}