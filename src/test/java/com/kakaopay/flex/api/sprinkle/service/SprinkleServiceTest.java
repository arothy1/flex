package com.kakaopay.flex.api.sprinkle.service;

import com.kakaopay.flex.api.sprinkle.vo.RequestSprinkle;
import com.kakaopay.flex.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.Assert;

@SpringBootTest
class SprinkleServiceTest {

    @Autowired SprinkleService sprinkleService;

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