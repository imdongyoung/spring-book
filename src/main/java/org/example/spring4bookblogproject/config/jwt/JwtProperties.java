package org.example.spring4bookblogproject.config.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties("jwt") // 자바 클래스에 프로퍼티 값을 가져와 사용하는 애너테이션
public class JwtProperties {
	private String issuer;
	private String secretKey;
}
