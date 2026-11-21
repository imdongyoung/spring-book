package org.example.spring4bookblogproject.service;

import org.example.spring4bookblogproject.domain.RefreshToken;
import org.example.spring4bookblogproject.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;

	public RefreshToken findByRefreshToken(String refreshToken) {
		return refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(
			() -> new IllegalArgumentException("Unexpected Token")
		);
	}
}
