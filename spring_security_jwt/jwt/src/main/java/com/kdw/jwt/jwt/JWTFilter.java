package com.kdw.jwt.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kdw.jwt.dto.CustomUserDetails;
import com.kdw.jwt.entity.UserEntity;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter{
	
	private final JWTUtil jwtUtil;
	
	public JWTFilter(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String accessToken = request.getHeader("access");
		System.out.println(accessToken);
		if(accessToken == null) {
			filterChain.doFilter(request, response);
			return;
		}
		
		try {
			jwtUtil.isExpired(accessToken);
		}catch(ExpiredJwtException e) {
			PrintWriter writer = response.getWriter();
			writer.print("access token expired");
			System.out.println("ex");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
				
		String category = jwtUtil.getCategory(accessToken);
		
		if(!category.equals("access")) {
			
			PrintWriter writer = response.getWriter();
			writer.print("invalid access token");
			
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String username = jwtUtil.getUsername(accessToken);
		String role = jwtUtil.getRole(accessToken);
		
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername(username);
		userEntity.setRole(role);
		CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
		
		Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,customUserDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);
		
		filterChain.doFilter(request, response);
	}

}
