package com.kdw.jwt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kdw.jwt.dto.JoinDto;
import com.kdw.jwt.service.JoinService;

@Controller
@ResponseBody
public class JoinController {

	private final JoinService joinService;
	
	public JoinController(JoinService joinService) {
		this.joinService = joinService;
	}
	@PostMapping("/join")
	public String joinProcess(JoinDto joinDto) {
		joinService.joinProcess(joinDto);
		return "ok";
	}
}
