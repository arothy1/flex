package com.kakaopay.flex.api.sprinkle.service;

import com.kakaopay.flex.api.sprinkle.entity.Sprinkle;
import com.kakaopay.flex.api.sprinkle.repository.SprinkleRepository;
import com.kakaopay.flex.api.user.entity.User;
import com.kakaopay.flex.api.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SprinkleServiceImplTest {

	@Autowired
	SprinkleRepository sprinkleRepository;

	@Autowired
	UserRepository userRepository;

	@BeforeEach
	public void setUp() {

		Random random = new Random();

		for (int i = 0; i < 10; i++) {
			userRepository.save(new User());
		}

		List<User> userList = userRepository.findAll();
		Optional<User> optionalAnyUser = userList.stream()
			.findAny();

		optionalAnyUser.ifPresent(anyUser -> {
			Sprinkle sprinkle = new Sprinkle();
			sprinkle.setMoney(10000);

			Long senderId = anyUser.getId();
			sprinkle.setSenderId(senderId);

			int receiverCount = random.nextInt(userList.size() - 1);
			List<User> receiverList = userList.stream()
				.filter(user -> user.getId() != senderId)
				.limit(receiverCount)
				.collect(Collectors.toList());
			sprinkle.setReceiverList(receiverList);
			sprinkleRepository.save(sprinkle);
		});
	}

	@Test
	void t_findAllSprinkle() {
//		System.out.println(userRepository.findAll());
		System.out.println(sprinkleRepository.findAll());
	}

}