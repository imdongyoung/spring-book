package org.example.spring4bookblogproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccessTokenRequest {
	private String refreshToken;
}
