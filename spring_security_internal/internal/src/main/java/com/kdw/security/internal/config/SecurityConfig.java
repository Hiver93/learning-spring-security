package com.kdw.security.internal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	
	// proxy에서 filterchain에 대한 requestMatcher가 맞는 것을 확인후 필터 선택 된다.
	// 설정하지 않으면 기본적으로 첫번째 필터체인만을 통과한다. 따라서 설정을 해주어야한다.
	
	// authorizeHttpRequests 의 requestMatcher만을 등록하면 두번째 체인은 적용되지 않는다.(이것은 인가작업)
	// securityMatchers에서 등록해주어야 한다.
	
	// security filter chain 등록시 순서는 @Order를 사용하면 된다. (낮은 숫자가 우선순위가 높다)
	
	@Bean
	@Order(1)
	public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception{
		
		http
			.securityMatchers((auth)->auth
					.requestMatchers("/user"));
		
		http
			.authorizeHttpRequests(auth-> auth
					.requestMatchers("/user").permitAll());
		
		return http.build();
	}
	
	@Bean
	@Order(2)
	public SecurityFilterChain filterChain2(HttpSecurity http) throws Exception{
		
		http
		.securityMatchers((auth)->auth
				.requestMatchers("/admin"));
		
		http
		.authorizeHttpRequests(auth-> auth
				.requestMatchers("/admin").permitAll());
		return http.build();
	}
	
	
	// 등록한 경로에 대해 시큐리티 필터를 적용하지 않게 한다.
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring().requestMatchers("/img/**");
	}
}
