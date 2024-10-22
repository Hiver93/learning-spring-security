package com.kdw.security.internal.controller;

import java.util.concurrent.Callable;

import org.springframework.security.core.context.SecurityContextHolder;
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
	
	
	// 필터단에 적용하는 필터가 동기화과정이 일어나는 것을 어떻게 아는가?
	// WebAsyncManagerIntergrationFilter는 현재 스레드의 SecurityContext를 다룰 수 있는 interceptor를 WebAsyncManager에 등록만 해준다.
	// 이 후 dispatcherServlet가 controller에서 callable을 받아서 다시 webAsyncManager에 전달하여 처리한다.(새로운 스레드에 context 복사하여 전달, callable은 그곳에서 해결)
	// 따라서 스레드가 바뀌더라도 SecurityContext에서 동일한 값을 획득할 수 있다.
	@GetMapping("/async")
	@ResponseBody
	public Callable<String> asyncPage(){
		System.out.println("start " + SecurityContextHolder.getContext().getAuthentication().getName());
		
		return () ->{
			Thread.sleep(4000);
			System.out.println("end" + SecurityContextHolder.getContext().getAuthentication().getName());
			return "async";
		};
	}
}
