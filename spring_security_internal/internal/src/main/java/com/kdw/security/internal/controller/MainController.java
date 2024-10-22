package com.kdw.security.internal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

	@GetMapping("/user")
	public String userPage() {
		return "user page";
	}
	
	@GetMapping("/admin")
	public String adminPage() {
		return "admin page";
	}
	
	@GetMapping("/filterbefore")
	public String before() {
//		return "forward:/filterafter";
		return "redirect:/filterafter";
	}
	
	@GetMapping("/filterafter")
	@ResponseBody
	public String after() {
		return "after";
	}
}
