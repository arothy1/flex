package com.kakaopay.flex.api.user.service;

import com.kakaopay.flex.api.user.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

	List<User> findAll();
	Optional<User> findById(Long Id);

}
