package com.kdw.jwt.jwt;

import java.io.IOException;

import org.springframework.web.filter.GenericFilterBean;

import com.kdw.jwt.repository.RefreshRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomLogoutFilter extends GenericFilterBean {

	private final JWTUtil jwtUtil;
	private final RefreshRepository refreshRepository;
	
	public CustomLogoutFilter(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
		this.jwtUtil = jwtUtil;
		this.refreshRepository = refreshRepository;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
		
	}
	
	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		String requestUri = request.getRequestURI();
		if(!requestUri.matches("^\\/logout$")) {
			chain.doFilter(request, response);
			return;
		}
		String requestMethod = request.getMethod();
		if(!requestMethod.equals("POST")) {
			chain.doFilter(request, response);
			return;
		}
		
		// get refresh token
		String refresh = null;
		Cookie[] cookies = request.getCookies();
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals("refresh")) {
				refresh = cookie.getValue();
			}
		}
		
		if(refresh == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		// refresh 인지 확인
		String category = jwtUtil.getCategory(refresh);
		if(!category.equals("refresh")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		Boolean isExist = refreshRepository.existsByRefresh(refresh);
		if(!isExist) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		
		// 로그아웃 진행
		// Refresh 토큰 DB에서 제거
		refreshRepository.deleteByRefresh(refresh);
		
		// refresh토큰 cookie 값 0
		Cookie cookie = new Cookie("refresh",null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		
		response.addCookie(cookie);
		response.setStatus(HttpServletResponse.SC_OK);
	}

}
