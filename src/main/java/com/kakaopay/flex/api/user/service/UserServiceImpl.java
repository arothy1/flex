package com.kakaopay.flex.api.user.service;

import com.kakaopay.flex.api.user.entity.User;
import com.kakaopay.flex.api.user.repository.UserRepository;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
		this.userRepository.save(User.builder().budget(10000).build());	//FIXME
	}

	UserRepository userRepository;

	@Override
	public List<User> findAll() {
		return null;
	}

	@Override
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}
}