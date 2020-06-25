package com.kakaopay.flex.api.user.service;

import com.kakaopay.flex.api.user.entity.User;
import com.kakaopay.flex.api.user.repository.UserRepository;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl {

	@Resource(name = "userRepository")
	UserRepository repository;


//	public UserServiceImpl(UserRepository repository) {
//		this.repository = repository;
//	}
}