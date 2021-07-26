package com.chinjja.app.user;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.chinjja.app.user.dto.UserCreateDto;
import com.chinjja.app.user.dto.UserEmailDto;
import com.chinjja.app.user.dto.UserPasswordDto;
import com.chinjja.app.user.dto.UserUpdateDto;

import lombok.RequiredArgsConstructor;
import lombok.val;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Validated
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper mapper = new ModelMapper() {{
		getConfiguration()
		.setSkipNullEnabled(true)
		.setFieldMatchingEnabled(true)
		.setFieldAccessLevel(AccessLevel.PRIVATE);
	}};
	
	@PostAuthorize("isAuthenticated() and returnObject?.email == principal.username")
	public Mono<User> byId(Long id) {
		return userRepository.findById(id);
	}
	
	@PostAuthorize("isAuthenticated() and returnObject?.email == principal.username")
	public Mono<User> byEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	public Mono<Boolean> existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}
	
	public Mono<User> create(@Valid UserCreateDto dto) {
		return userRepository.existsByEmail(dto.getEmail())
			.flatMap(exists -> {
				if(exists) {
					return Mono.error(new IllegalArgumentException("email already exists"));
				}
				else {
					val user = User.builder().build();
					val encoded = passwordEncoder.encode(dto.getPassword());
					mapper.map(dto, user);
					return userRepository.save(user.withPassword(encoded));
				}
			});
	}
	
	@PreAuthorize("isAuthenticated() and #user.email == principal.username")
	public Mono<User> update(User user, @Valid UserUpdateDto dto) {
		if(user.getId() == null) {
			return Mono.error(new IllegalArgumentException("cannot find user"));
		}
		else {
			mapper.map(dto, user);
			return userRepository.save(user);
		}
	}
	
	@PreAuthorize("isAuthenticated() and #user.email == principal.username")
	public Mono<User> updateEmail(User user, @Valid UserEmailDto dto) {
		return Mono.empty();
	}
	
	@PreAuthorize("isAuthenticated() and #user.email == principal.username")
	public Mono<User> updatePassword(User user, @Valid UserPasswordDto dto) {
		return Mono.empty();
	}
}
