package com.kdw.security.internal.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


// 한번의 요청에 한번만 수행된다.
// redirect는 클라이언트에서 다시 요청하도록 하기 때문에 다시 수행되고, forward는 서버내에서 처리하기 떄문에 다시 수행되지 않는다.
public class CustomOnceFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		System.out.println("CustomOnceFilter");
		filterChain.doFilter(request, response);
	}

}
