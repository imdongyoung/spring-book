package org.example.spring4bookblogproject.service;

import org.example.spring4bookblogproject.domain.User;
import org.example.spring4bookblogproject.dto.AddUserRequest;
import org.example.spring4bookblogproject.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public Long save(AddUserRequest dto) {
		return userRepository.save(User.builder()
				.email(dto.getEmail())
				.password(passwordEncoder.encode(dto.getPassword()))
				.build())
			.getId();
	}

	public User findById(Long userId) {
		return userRepository.findById(userId).orElseThrow(
			() -> new IllegalArgumentException("Unexpected user")
		);
	}
}
