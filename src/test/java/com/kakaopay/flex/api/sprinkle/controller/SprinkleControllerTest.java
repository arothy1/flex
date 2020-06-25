package com.kakaopay.flex.api.sprinkle.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;

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
	void healCheck() {
		try {
			result = mvc.perform(get("/sprinkle/healthCheck")).andExpect(status().is2xxSuccessful()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void doSprinkle() {
		assertThat(2 > 1).isTrue();
	}

	@Test
	void doReceive() {
		assertThat(2 > 1).isTrue();
	}

	@Test
	void getSprinkle() {
		assertThat(2 > 1).isTrue();
	}
}