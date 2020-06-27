package com.kakaopay.flex.api.sprinkle.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.flex.api.sprinkle.vo.RequestSprinkle;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class SprinkleControllerTest {

	@Autowired
	private SprinkleController controller;

	private MockHttpServletRequest request;

	@Autowired
	MockMvc mvc;

	private MvcResult result;

	@BeforeEach
	void setUp() {
		request = new MockHttpServletRequest();
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void healCheck() throws Exception {
		result = mvc.perform(get("/sprinkle/healthCheck"))
				.andExpect(status().is2xxSuccessful())
				.andReturn();
	}

	@Test
	public void doSprinkle() throws Exception {
		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

		String content = jsonMapper.writeValueAsString(RequestSprinkle.builder()
				.sprinkleMoney(10000)
				.receiveUserCount(5));

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-USER-ID", "1");
		headers.set("X-ROOM-ID", "klwjgawo15sadf");
		headers.setContentType(MediaType.APPLICATION_JSON);

		result = mvc.perform(post("/sprinkle")
				.content(content)
				.headers(headers))
//				.andExpect(status().is2xxSuccessful())
				.andReturn();
	}

	@Test
	public void doReceive() throws Exception {
		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

		String content = jsonMapper.writeValueAsString(RequestSprinkle.builder()
				.token("Aa1"));

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-USER-ID", "1");
		headers.set("X-ROOM-ID", "klwjgawo15sadf");
		headers.setContentType(MediaType.APPLICATION_JSON);

		result = mvc.perform(put("/sprinkle")
				.content(content)
				.headers(headers))
//				.andExpect(status().is2xxSuccessful())
				.andReturn();
	}

	@Test
	void getSprinkle() throws Exception {
		result = mvc.perform(get("/sprinkle"))
//				.andExpect(status().is2xxSuccessful())
				.andReturn();
	}

}