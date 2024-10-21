package com.kdw.security.internal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class MainController {

	@GetMapping("/user")
	public String userPage() {
		return "user page";
	}
	
	@GetMapping("/admin")
	public String adminPage() {
		return "admin page";
	}
}
