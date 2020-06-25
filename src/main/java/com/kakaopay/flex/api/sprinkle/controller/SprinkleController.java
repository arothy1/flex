package com.kakaopay.flex.api.sprinkle.controller;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/sprinkle")
@Slf4j
public class SprinkleController {

	@GetMapping(value = "/healthCheck")
	public Object healthCheck() {
		return "sprinkle is run!";
	}

	@PostMapping(value="")
	public Object doSprinkle(HttpServletRequest request) {
		return request.getRequestURI();
	}

	@PutMapping(value="")
	public Object doReceive(HttpServletRequest request) {
		return request.getRequestURI();
	}

	@GetMapping(value="")
	public Object getSprinkle(HttpServletRequest request) {
		return request.getRequestURI();
	}

}