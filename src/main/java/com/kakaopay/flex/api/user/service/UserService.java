package com.kakaopay.flex.api.user.service;

import com.kakaopay.flex.api.user.entity.User;
import java.util.List;

public interface UserService {

	List<User> findAll();
	User findById(Long Id);

}
