package com.kdw.jwt.controller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kdw.jwt.entity.RefreshEntity;
import com.kdw.jwt.jwt.JWTUtil;
import com.kdw.jwt.repository.RefreshRepository;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@ResponseBody
public class ReissueController {
	private final JWTUtil jwtUtil;
	private final RefreshRepository refreshRepository;
	
	public ReissueController(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
		this.jwtUtil = jwtUtil;
		this.refreshRepository = refreshRepository;
	}
	
	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response){
		String refresh = null;
		Cookie[] cookies = request.getCookies();
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals("refresh")) {
				refresh = cookie.getValue();
			}
		}
		if(refresh == null) {
			return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
		}
		
		try {
			jwtUtil.isExpired(refresh);
		}catch(ExpiredJwtException e) {
			return new ResponseEntity<>("access token expired",HttpStatus.BAD_REQUEST);
		}
		
		String category = jwtUtil.getCategory(refresh);
		
		if(!category.equals("refresh")) {
			return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		}
		
		Boolean isExist = refreshRepository.existsByRefresh(refresh);
		if(!isExist) {
			return new ResponseEntity<>("invalid refresh token",HttpStatus.BAD_REQUEST);
		}
		
		String username = jwtUtil.getUsername(refresh);
		String role = jwtUtil.getRole(refresh);
		
		// make new JWT
		String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
		String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);
		
		refreshRepository.deleteByRefresh(refresh);
		addRefreshEntity(username, newRefresh, 86400000L);
		
		// reponse
		response.setHeader("access", newAccess);
		response.addCookie(createCookie("refresh", newRefresh));
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	private Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key,value);
		cookie.setMaxAge(24*60*60);
//		cookie.setSecure(true);
//		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}
	private void addRefreshEntity(String username, String refresh, Long expiredMs) {
		Date date = new Date(System.currentTimeMillis()+expiredMs);
		
		RefreshEntity refreshEntity = new RefreshEntity();
		refreshEntity.setUsername(username);
		refreshEntity.setRefresh(refresh);
		refreshEntity.setExpiration(date.toString());
		
		refreshRepository.save(refreshEntity);
	}
	
	
}
