package com.kdw.security.internal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.session.DisableEncodeUrlFilter;

import com.kdw.security.internal.filter.CustomGenericFilter;
import com.kdw.security.internal.filter.CustomOnceFilter;

// 필터 간단 개요 (위의 네가지는 필수적으로 필요)
// DisableEncodeUrlFilter URL로 간주되지 않느 부분을 포함하지 않도록 설정
// WebAsyncManagerIntegrationFIlter 비동기로 처리되는 작업에 알맞은 시큐리티 컨텍스트(세션)을 적용
// SecurityContextHolderFilter 접근한 유저에 대해 시큐리티 컨텍스트 관리
// HeaderWriterFilter 보안을 위한 응답 헤더 추가 (X-Frame-Option, X-XXS-Protection and X-Content-Type-Options)
// CorsFilter CORS 설정 필터
// CsrfFilter CSRF 방어 피러
// LogoutFilter 로그아웃 처리 시작점 GET:"/logout"
// UsernamePasswordAuthenticationFIlter username/password 기반 로그인 처리 시작점 POST:"/login"
// DefaultLoginPageGeneratingFilter
// DefaultLogoutPageGeneratingFilter
// BasicAuthenticationFilter http basic 기반 로그인 처리 시작점
// RequestCachAwareFilter 이전 요청 정보가 존재하면 처리 후 현재 요청 판다
// SecurityCOntextHolderAwareREquestFilter ServletRequest에 서블릿 API보안을 구현
// AnonymousAuthenticationFIlter 최초 접속으로 인증 정보가 없고, 인증을하지 않았을 경우 세ㅕㅅㄴ에 익병 사용자 설정
// ExceptionTranslationFilter 인증 및 접근 예외에 대한 처리
// AuthorizationFilter 경로 및 권한별 인가 (구. filterSecurityIntercepter)


@Configuration
@EnableWebSecurity(debug = true) // debug모드를 true로 하면 필터체인의 어떤 필터를 거쳐 요청이 지나가는지 출력해줌
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
		
		// cors 기본적으로 활성화
		http
			.cors((cors)->cors.disable());
		
		// csrf 기본적으로 활성화
		http
			.csrf((csrf)->csrf.disable());
		
		// usernamePasswordAuthentiation은 기본적으로 비활성화
		http
			.formLogin(Customizer.withDefaults()); // 이것으로 활성화
		
		// 로그인, 로그아웃 페이지는 기본적으로 활성화
		http
			.formLogin((login)->login.loginPage("/login").permitAll());
		
		// httpBasic
		http
			.httpBasic(Customizer.withDefaults()); // 활성화
		
		// 커스텀 필터 등록
//		http
//			.addFilterBefore(추가할 필터,기존필터.class);
//		http
//			.addFilterAt(추가할 필터,기존필터.class);
//		http
//			.addFilterAfter(추가할 필터,기존필터.class);
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
	
	@Bean
	@Order(3)
	public SecurityFilterChain filterChain3(HttpSecurity http) throws Exception{

		http
			.securityMatchers((auth)->auth
					.requestMatchers("/filterafter", "/filterbefore"));
		
		http
			.authorizeHttpRequests(auth-> auth
					.anyRequest().permitAll());
		
		http
			.addFilterAfter(new CustomGenericFilter(), LogoutFilter.class);
		
		http
			.addFilterAfter(new CustomOnceFilter(), LogoutFilter.class);
		
		return http.build();
	}
	
	
	// 등록한 경로에 대해 시큐리티 필터를 적용하지 않게 한다.
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring().requestMatchers("/img/**");
	}
}
