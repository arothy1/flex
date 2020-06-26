package com.kakaopay.flex.api.sprinkle.controller;

import javax.servlet.http.HttpServletRequest;

import com.kakaopay.flex.api.sprinkle.service.SprinkleService;
import com.kakaopay.flex.api.sprinkle.vo.RequestSprinkle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;

@RestController
@RequestMapping(value="/sprinkle")
@Slf4j
public class SprinkleController {

	private SprinkleService sprinkleService;

	public SprinkleController(SprinkleService sprinkleService) {
		this.sprinkleService = sprinkleService;
	}

	@GetMapping(value = "/healthCheck")
	public Object healthCheck() {
		return "sprinkle is run!";
	}

	@PostMapping(value="")
	public Object doSprinkle(@RequestBody RequestSprinkle requestSprinkle,
							 @RequestHeader("X-USER-ID") String xUserId,
							 @RequestHeader("X-ROOM-ID") String xRoomId) throws Exception {
		try {
			requestSprinkle.setXUserId(Long.parseLong(xUserId));
			requestSprinkle.setXRoomId(xRoomId);
			return ResponseEntity.ok(sprinkleService.doSprinkle(requestSprinkle));
		} catch (NumberFormatException  | DuplicateKeyException e) {
			log.error("{}", e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		}
	}

	@PutMapping(value="")
	public Object doReceive(@RequestBody RequestSprinkle requestSprinkle,
							@RequestHeader("X-USER-ID") String xUserId) throws Exception {
		try {
			requestSprinkle.setXUserId(Long.parseLong(xUserId));
			return ResponseEntity.ok(sprinkleService.doReceive(requestSprinkle));
		} catch (ServerErrorException e) {
			log.error("{}", e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		}
	}

	@GetMapping(value="")
	public Object getSprinkle(HttpServletRequest request) {
		return request.getRequestURI();
	}

	@ExceptionHandler(Exception.class)
	public Object handleException() {
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY);
	}

}