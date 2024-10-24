package com.kdw.jwt.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.kdw.jwt.jwt.CustomLogoutFilter;
import com.kdw.jwt.jwt.JWTFilter;
import com.kdw.jwt.jwt.JWTUtil;
import com.kdw.jwt.jwt.LoginFilter;
import com.kdw.jwt.repository.RefreshRepository;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;
	private final RefreshRepository refreshRepository;
	
	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
		this.authenticationConfiguration = authenticationConfiguration;
		this.jwtUtil = jwtUtil;
		this.refreshRepository = refreshRepository;
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChin(HttpSecurity http) throws Exception {
		
		
		http
			.cors((cors) -> cors
				.configurationSource(new CorsConfigurationSource() {

					@Override
					public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
						CorsConfiguration corsConfiguration = new CorsConfiguration();
						
						corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:5500"));
						corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
						corsConfiguration.setAllowCredentials(true);
						corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
						corsConfiguration.setMaxAge(3600L);
						
						corsConfiguration.setExposedHeaders(Collections.singletonList("Authorization"));
						return corsConfiguration;
					}
					
				}));

		http
			.csrf((auth)->auth.disable());
		
		// From 로그인 방식 disable
		http
			.formLogin((auth)->auth.disable());
		
		//http basic 인증 방식 
		http 
			.httpBasic((auth) -> auth.disable());
		
		http
			.authorizeHttpRequests((auth) -> auth
					.requestMatchers("/login", "/", "/join").permitAll()
					.requestMatchers("/admin").hasAnyRole("ADMIN")
					.requestMatchers("/reissue").permitAll()
					.anyRequest().authenticated()
			);
		
		//jwtfilter 등록
		http
			.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
		
		http
			.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository), UsernamePasswordAuthenticationFilter.class);
		
		http
			.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

			
		
		// 세션 설정
		http
			.sessionManagement(session -> session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			);
		return http.build();
	}
}
