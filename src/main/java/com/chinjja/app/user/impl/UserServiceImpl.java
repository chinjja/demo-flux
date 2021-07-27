package com.chinjja.app.user.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chinjja.app.user.User;
import com.chinjja.app.user.UserRepository;
import com.chinjja.app.user.UserRole;
import com.chinjja.app.user.UserRoleRepository;
import com.chinjja.app.user.UserService;
import com.chinjja.app.user.dto.UserCreateDto;
import com.chinjja.app.user.dto.UserEmailDto;
import com.chinjja.app.user.dto.UserPasswordDto;
import com.chinjja.app.user.dto.UserRoleDto;
import com.chinjja.app.user.dto.UserUpdateDto;

import lombok.RequiredArgsConstructor;
import lombok.val;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserRoleRepository userRoleRepository;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper mapper = new ModelMapper() {{
		getConfiguration()
		.setSkipNullEnabled(true)
		.setFieldMatchingEnabled(true)
		.setFieldAccessLevel(AccessLevel.PRIVATE);
	}};
	
	@Override
	public Mono<User> byId(Long id) {
		return userRepository.findById(id);
	}
	
	@Override
	public Mono<User> byEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	@Override
	public Mono<Boolean> existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}
	
	@Override
	public Mono<User> create(UserCreateDto dto) {
		return userRepository.existsByEmail(dto.getEmail())
			.flatMap(exists -> {
				if(exists) {
					return Mono.error(new IllegalArgumentException("email already exists"));
				}
				else {
					val user = User.builder().build();
					val encoded = passwordEncoder.encode(dto.getPassword());
					mapper.map(dto, user);
					return userRepository.save(user.withPassword(encoded))
							.flatMap(u -> addRole(UserRoleDto.builder()
									.email(u.getEmail())
									.role("USER")
									.build())
									.map(x -> u));
				}
			});
	}
	
	@Override
	public Mono<User> init() {
		return userRepository.count()
				.filter(count -> count == 0L)
				.flatMap(x -> create(UserCreateDto.builder()
						.email("root@user.com")
						.password("12345678")
						.name("root")
						.build()))
				.zipWhen(user -> addRole(UserRoleDto.builder()
						.email(user.getEmail())
						.role("ADMIN")
						.build()))
				.map(t -> t.getT1());
	}
	
	@Override
	public Mono<User> update(User user, UserUpdateDto dto) {
		if(user.getId() == null) {
			return Mono.error(new IllegalArgumentException("cannot find user"));
		}
		else {
			mapper.map(dto, user);
			return userRepository.save(user);
		}
	}
	
	@Override
	public Mono<User> updateEmail(User user, UserEmailDto dto) {
		return Mono.empty();
	}
	
	@Override
	public Mono<User> updatePassword(User user, UserPasswordDto dto) {
		return Mono.empty();
	}
	
	@Override
	public Mono<UserRole> addRole(UserRoleDto dto) {
		return userRepository.findByEmail(dto.getEmail())
				.flatMap(user -> userRoleRepository
						.findByUserAndRole(user, dto.getRole())
						.switchIfEmpty(userRoleRepository.save(UserRole.builder()
								.user(user)
								.role(dto.getRole())
								.build())));
	}
	
	@Override
	public Mono<User> removeRole(UserRoleDto dto) {
		return userRepository.findByEmail(dto.getEmail())
				.zipWhen(user -> userRoleRepository.deleteByUserAndRole(user, dto.getRole()))
				.map(t -> t.getT1());
	}
	
	@Override
	public Flux<String> getRoles(User user) {
		return userRoleRepository.findAllRoleByUser(user);
	}
}
