package com.kakaopay.flex.api.sprinkle.controller;

import javax.servlet.http.HttpServletRequest;

import com.kakaopay.flex.api.sprinkle.service.SprinkleService;
import com.kakaopay.flex.api.sprinkle.vo.RequestSprinkle;
import com.kakaopay.flex.exception.InvalidRequestException;
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
	public String healthCheck() {
		return "sprinkle is run!";
	}

	@PostMapping
	public ResponseEntity doSprinkle(@RequestBody RequestSprinkle requestSprinkle,
							 @RequestHeader("X-USER-ID") long xUserId,
							 @RequestHeader("X-ROOM-ID") String xRoomId) {
		try {
			requestSprinkle.setXUserId(xUserId);
			requestSprinkle.setXRoomId(xRoomId);
			return ResponseEntity.ok(sprinkleService.doSprinkle(requestSprinkle));
		} catch (DuplicateKeyException e) {
			log.error("{}", e);
			throw new ResponseStatusException(HttpStatus.ACCEPTED, e.getMessage(), e);
		}
	}

	@PutMapping
	public ResponseEntity doReceive(@RequestBody RequestSprinkle requestSprinkle,
							@RequestHeader("X-USER-ID") long xUserId,
							@RequestHeader("X-ROOM-ID") String xRoomId) {
		try {
			requestSprinkle.setXUserId(xUserId);
			requestSprinkle.setXRoomId(xRoomId);
			return ResponseEntity.ok(sprinkleService.doReceive(requestSprinkle));
		} catch (InvalidRequestException e) {
			log.error("{}", e);
			throw new ResponseStatusException(HttpStatus.ACCEPTED, e.getMessage(), e);
		}
	}

	@GetMapping(value = "/{token}")
	public ResponseEntity getSprinkle(@PathVariable String token,
							  @RequestHeader("X-USER-ID") long xUserId,
							  @RequestHeader("X-ROOM-ID") String xRoomId) {
		try {
			RequestSprinkle requestSprinkle = RequestSprinkle.builder()
					.xUserId(xUserId)
					.xRoomId(xRoomId)
					.token(token)
					.build();
			return ResponseEntity.ok(sprinkleService.getSprinkle(requestSprinkle));
		} catch (InvalidRequestException e) {
			log.error("{}", e);
			throw new ResponseStatusException(HttpStatus.ACCEPTED, e.getMessage(), e);
		}
	}

}