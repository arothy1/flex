package com.kakaopay.flex.api.user.service;

import static org.junit.jupiter.api.Assertions.*;

import com.kakaopay.flex.api.user.entity.User;
import com.kakaopay.flex.api.user.repository.UserRepository;
import javax.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceImplTest {

	@Resource(name = "userRepository")
	UserRepository userRepository;

	@BeforeEach
	void setUp() {
	}

	@Test
	void t1() {
		userRepository.save(new User());
		userRepository.save(new User());
		userRepository.save(new User());


		System.out.println(userRepository.findAll());
	}
}