package org.example.spring4bookblogproject.config.jwt;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.example.spring4bookblogproject.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TokenProvider {
	private final JwtProperties jwtProperties;

	public String generateToken(User user, Duration expiredAt) {
		Date now = new Date();
		return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
	}

	/**
	 * 1. JWT 토큰 생성 메서드
	 * 인자 : 만료 시간, 유저 정보
	 * 값
	 * - 헤더의 typ(타입)
	 * 내용
	 * - iss(발급자)
	 * - iat(발급 일시)
	 * - exp(만료 일시)
	 * - sub(토큰 제목)
	 * 클래임
	 * - 유저ID
	 */
	private String makeToken(Date expiry, User user) {
		Date now = new Date();

		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ : JWT
			// 내용 iss : imdong207@gmail.com(properties 파일에서 설정한 값)
			.setIssuer(jwtProperties.getIssuer())
			.setIssuedAt(now) // 내용 iat : 현재 시간
			.setExpiration(expiry) // 내용 exp : expiry 멤버 변수값
			.setSubject(user.getEmail()) // 내용 sub : 유저의 이메일
			.claim("id", user.getId()) // 클레임 id : 유저 ID
			// 서명 : 비밀값과 함께 해시값을 HS256 방식으로 암호화
			.signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
			.compact();

	}

	/**
	 * 2. JWT 토큰 유효성 검증 메서드
	 * - application.yaml의 jwt 프로퍼티에 선언한 비밀값과 함께 토큰 복호화를 진행
	 * - 복호화 과정에서 에러가 나면 유효하지 않은 값
	 */
	public boolean validToken(String token) {
		try {
			Jwts.parser()
				.setSigningKey(jwtProperties.getSecretKey()) // 비밀값으로 복호화
				.parseClaimsJws(token);

			return true;
		} catch (Exception e) { // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
			return false;
		}
	}

	/**
	 * 3. 토큰 기반으로 인증 정보를 가져오는 메서드
	 * - getClaims로 클레임 정보를 받음
	 * - 받은 클레임 정보로 사용자 이메일이 들어있는 sub와 토큰 기반으로 인증 정보 생성
	 * = UsernamePasswordAuthenticationToken에 첫 인자 User는 프로젝트의 User 클래스가 아닌
	 * 스프링 시큐리티에서 제공하는 객체인 User 클래스
	 */
	public Authentication getAuthentication(String token) {
		Claims claims = getClaims(token);
		Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

		return new UsernamePasswordAuthenticationToken(
			new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities), token,
			authorities);
	}

	/**
	 * 4, 토큰 기반으로 유저 ID를 가져오는 메서드
	 */
	public Long getUserId(String token) {
		Claims claims = getClaims(token);
		return claims.get("id", Long.class);
	}

	/**
	 * 5. 토큰을 받아 프로퍼티의 비밀값으로 복호화한 뒤 클레임을 반환하는 메서드
	 */
	public Claims getClaims(String token) {
		return Jwts.parser() // 클레임 조회
			.setSigningKey(jwtProperties.getSecretKey())
			.parseClaimsJws(token)
			.getBody();
	}
}
