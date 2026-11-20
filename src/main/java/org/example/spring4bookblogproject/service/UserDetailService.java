package org.example.spring4bookblogproject.service;

import org.example.spring4bookblogproject.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {
	private final UserRepository userRepository;

	// 사용자 이름(email)로 사용자의 정보를 가져오는 메서드
	@Override
	public UserDetails loadUserByUsername(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException((email)));
	}

}
