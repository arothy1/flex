package com.kakaopay.flex.api.sprinkle.service;

import com.kakaopay.flex.api.pick.entity.Pick;
import com.kakaopay.flex.api.pick.repository.PickRepository;
import com.kakaopay.flex.api.sprinkle.entity.Sprinkle;
import com.kakaopay.flex.api.sprinkle.repository.SprinkleRepository;
import com.kakaopay.flex.api.sprinkle.vo.RequestSprinkle;
import com.kakaopay.flex.api.sprinkle.vo.ResponseSprinkle;
import com.kakaopay.flex.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SprinkleServiceTest {

    @Autowired SprinkleService sprinkleService;
    @Autowired SprinkleRepository sprinkleRepository;
    @Autowired PickRepository pickRepository;

    private long xUserId = 1;
    private long xUserId2 = 2;
    private String xRoomId = "ItIsRoom!!!";

    @Test
    void t1_doSprinkle() {
        try {
            RequestSprinkle requestSprinkle = RequestSprinkle.builder()
                    .xUserId(xUserId)
                    .xRoomId(xRoomId)
                    .sprinkleMoney(10000)
                    .receiveUserCount(7)
                    .build();
            sprinkleService.doSprinkle(requestSprinkle);
            Assert.state(true, "성공");
        } catch (DuplicateKeyException e) {
            Assert.state(true, e.getMessage());
        }

    }

    @Test
    void t2_doReceive() {
        RequestSprinkle requestSprinkle1 = RequestSprinkle.builder()
                .xUserId(xUserId)
                .xRoomId(xRoomId)
                .sprinkleMoney(10000)
                .receiveUserCount(7)
                .build();
        String token = sprinkleService.doSprinkle(requestSprinkle1);

        try {
            RequestSprinkle requestSprinkle2 = RequestSprinkle.builder()
                    .token(token)
                    .xUserId(xUserId2)
                    .xRoomId(xRoomId)
                    .build();
            sprinkleService.doReceive(requestSprinkle2);
            Assert.state(true, "성공");
        } catch (InvalidRequestException e) {
            Assert.state(true, e.getMessage());
        }
    }

    @Test
    void t3_getSprinkle() {
        RequestSprinkle requestSprinkle1 = RequestSprinkle.builder()
                .xUserId(xUserId)
                .xRoomId(xRoomId)
                .sprinkleMoney(10000)
                .receiveUserCount(7)
                .build();
        String token = sprinkleService.doSprinkle(requestSprinkle1);

        RequestSprinkle requestSprinkle2 = RequestSprinkle.builder()
                .token(token)
                .xUserId(xUserId2)
                .xRoomId(xRoomId)
                .build();
        sprinkleService.doReceive(requestSprinkle2);

        try {
            RequestSprinkle requestSprinkle = RequestSprinkle.builder()
                    .token(token)
                    .xUserId(xUserId)
                    .xRoomId(xRoomId)
                    .build();
            sprinkleService.getSprinkle(requestSprinkle);
            Assert.state(true, "성공");
        } catch (InvalidRequestException e) {
            Assert.state(true, e.getMessage());
        }
    }
}