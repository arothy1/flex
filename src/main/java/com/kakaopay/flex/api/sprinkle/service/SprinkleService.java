package com.kakaopay.flex.api.sprinkle.service;

import com.kakaopay.flex.api.sprinkle.vo.RequestSprinkle;

public interface SprinkleService {

	String doSprinkle(RequestSprinkle requestSprinkle) throws Exception;

    Object doReceive(RequestSprinkle requestSprinkle) throws Exception;
}
