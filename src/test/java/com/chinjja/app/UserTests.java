package com.chinjja.app;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import com.chinjja.app.user.UserRepository;
import com.chinjja.app.user.UserRole;
import com.chinjja.app.user.UserRoleRepository;
import com.chinjja.app.user.UserService;
import com.chinjja.app.user.dto.UserCreateDto;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
public class UserTests {
	@Autowired
	UserService userService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	UserRoleRepository userRoleRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Test
	@WithMockUser("test@user.com")
	void user() {
		userService.create(UserCreateDto.builder()
				.email("test@user.com")
				.password("12345678")
				.name("test")
				.build())
		.concatWith(userService.byEmail("test@user.com"))
		.as(Transaction::rollback)
		.as(StepVerifier::create)
		.assertNext(user -> {
			assertThat(user.getEmail()).isEqualTo("test@user.com");
			assertThat(passwordEncoder.matches("12345678", user.getPassword())).isTrue();
			assertThat(user.getName()).isEqualTo("test");
			assertThat(user.getId()).isNotNull();
		})
		.assertNext(user -> {
			assertThat(user.getEmail()).isEqualTo("test@user.com");
			assertThat(passwordEncoder.matches("12345678", user.getPassword())).isTrue();
			assertThat(user.getName()).isEqualTo("test");
			assertThat(user.getId()).isNotNull();
		})
		.verifyComplete();
	}
	
	@Test
	void userRole() {
		userService.create(UserCreateDto.builder()
				.email("test@user.com")
				.password("12345678")
				.name("test")
				.build())
		.flatMapMany(user -> Flux.just("USER", "ADMIN")
				.flatMap(role -> userRoleRepository.save(UserRole.builder()
						.user(user)
						.role(role)
						.build())))
		.buffer()
		.flatMap(x -> userRepository.findByEmail("test@user.com")
				.flatMapMany(user -> userRoleRepository.findAllRoleByUser(user)))
		.buffer()
		.as(Transaction::rollback)
		.as(StepVerifier::create)
		.assertNext(roles -> {
			assertThat(roles).hasSize(2);
			assertThat(roles).contains("USER", "ADMIN");
		})
		.verifyComplete();
	}
}
