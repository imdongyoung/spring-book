package org.example.spring4bookblogproject.config;

import static org.springframework.boot.security.autoconfigure.web.servlet.PathRequest.*;

import org.example.spring4bookblogproject.config.jwt.TokenProvider;
import org.example.spring4bookblogproject.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import org.example.spring4bookblogproject.config.oauth.OAuth2SuccessHandler;
import org.example.spring4bookblogproject.config.oauth.OAuth2UserCustomService;
import org.example.spring4bookblogproject.repository.RefreshTokenRepository;
import org.example.spring4bookblogproject.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {
	private final OAuth2UserCustomService oAuth2UserCustomService;
	private final TokenProvider tokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserService userService;

	@Bean
	public WebSecurityCustomizer configure() { // 스프링 시큐리티 기능 비홣성화
		return (web) -> web.ignoring()
			.requestMatchers(toH2Console())
			.requestMatchers(
				"/img/**",
				"/css/**",
				"/js/**"
			);
	}

	// 토큰 방식으로 인증하기 때문에 기존에 사용하던 폼 로그인, 세션 비활성화
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// 헤더를 확인할 커스텀 필터 추가
			.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
			// 토큰 재발급 URL은 인증 없이 접근 가능하도록 설정. 나머지는 인증 필요
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/token").permitAll()
				.requestMatchers("/api/**").authenticated()
				.anyRequest().permitAll())
			.oauth2Login(oauth2 -> oauth2
				.loginPage("/login")
				// Authorization 요청과 관련된 상태 저장
				.authorizationEndpoint(authorizationEndpoint ->
					authorizationEndpoint.authorizationRequestRepository(
						oAuth2AuthorizationRequestBasedOnCookieRepository()))
				.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(oAuth2UserCustomService))
				// 인증 성공 시 실행할 핸들러
				.successHandler(oAuth2SuccessHandler())
			)
			.exceptionHandling(ex -> ex
				.defaultAuthenticationEntryPointFor(
					new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
					request -> request.getRequestURI().startsWith("/api/")
				)).build();
	}

	@Bean
	public OAuth2SuccessHandler oAuth2SuccessHandler() {
		return new OAuth2SuccessHandler(tokenProvider, refreshTokenRepository,
			oAuth2AuthorizationRequestBasedOnCookieRepository(), userService);
	}

	@Bean
	public TokenAuthenticationFilter tokenAuthenticationFilter() {
		return new TokenAuthenticationFilter(tokenProvider);
	}

	@Bean
	public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
		return new OAuth2AuthorizationRequestBasedOnCookieRepository();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

// import static org.springframework.boot.security.autoconfigure.web.servlet.PathRequest.*;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
// import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
//
// import lombok.RequiredArgsConstructor;
//
// @Configuration
// @EnableWebSecurity
// @RequiredArgsConstructor
// public class WebSecurityConfig {
// 	// 정적 리소스 및 h2 콘손 스프링 시큐리티 기능 비활성화
// 	@Bean
// 	public WebSecurityCustomizer configure() {
// 		return (web) -> web.ignoring()
// 			.requestMatchers(toH2Console())
// 			.requestMatchers("/static/**");
// 	}
//
// 	// 특정 HTTP 요청에 대한 웹 기반 보안 구성
// 	@Bean
// 	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
// 		return http
// 			.authorizeHttpRequests(auth -> auth
// 				.requestMatchers(
// 					"/login",
// 					"/signup",
// 					"/user"
// 				).permitAll()
// 				.anyRequest().authenticated())
// 			.formLogin(formLogin -> formLogin
// 				.loginPage("/login")
// 				.defaultSuccessUrl("/articles")
// 			)
// 			.logout(logout -> logout
// 				.logoutSuccessUrl("/login")
// 				.invalidateHttpSession(true)
// 			)
// 			.csrf(AbstractHttpConfigurer::disable)
// 			.build();
// 	}
//
// 	// 패스워드 인코더로 사용할 빈 등록
// 	@Bean
// 	public PasswordEncoder passwordEncoder() {
// 		return new BCryptPasswordEncoder();
// 	}
//
// }
