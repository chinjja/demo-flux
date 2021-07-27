package com.chinjja.app;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import com.chinjja.app.user.UserService;
import com.chinjja.app.user.dto.UserCreateDto;
import com.chinjja.app.user.dto.UserRoleDto;

import reactor.test.StepVerifier;

@SpringBootTest
public class UserTests {
	@Autowired
	UserService userService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	Transaction tx;
	
	@Test
	@WithMockUser("root@user.com")
	void shouldExistsRootUserOnInit() {
		userService.byEmail("root@user.com")
		.as(StepVerifier::create)
		.expectNextCount(1)
		.verifyComplete();
	}
	
	@Test
	@WithMockUser("root@user.com")
	void shouldExistsRootUserOnInit2() {
		userService.byEmail("root@user.com")
		.flatMapMany(user -> userService.getRoles(user))
		.buffer()
		.as(StepVerifier::create)
		.assertNext(roles -> {
			assertThat(roles).hasSize(2).contains("USER", "ADMIN");
		})
		.verifyComplete();
	}
	
	@Test
	@WithAnonymousUser
	void whenSearchUserByEmailWithoutAuth_thenFail() {
		userService.byEmail("root@user.com")
		.as(StepVerifier::create)
		.verifyError();
	}
	
	@Test
	void givenGoodArgument_whenCreateUser_thenShouldCreateNewUser() {
		userService.create(UserCreateDto.builder()
				.email("test@user.com")
				.password("12345678")
				.name("test")
				.build())
		.as(tx::rollback)
		.as(StepVerifier::create)
		.assertNext(user -> {
			assertThat(user.getEmail()).isEqualTo("test@user.com");
			assertThat(passwordEncoder.matches("12345678", user.getPassword())).isTrue();
			assertThat(user.getName()).isEqualTo("test");
			assertThat(user.getId()).isNotNull();
		})
		.verifyComplete();
	}
	
	@Test
	@WithMockUser("test@user.com")
	void givenEmail_whenFindByEmail_thenShouldReturnUser() {
		userService.create(UserCreateDto.builder()
				.email("test@user.com")
				.password("12345678")
				.name("test")
				.build())
		.flatMap(x -> userService.byEmail("test@user.com"))
		.as(tx::rollback)
		.as(StepVerifier::create)
		.assertNext(user -> {
			assertThat(user.getEmail()).isEqualTo("test@user.com");
			assertThat(passwordEncoder.matches("12345678", user.getPassword())).isTrue();
			assertThat(user.getName()).isEqualTo("test");
			assertThat(user.getId()).isNotNull();
		})
		.verifyComplete();
	}
	
	@Test
	@WithMockUser("user@user.com")
	void givenOtherUser_whenFindByEmail_thenShouldFail() {
		userService.create(UserCreateDto.builder()
				.email("test@user.com")
				.password("12345678")
				.name("test")
				.build())
		.flatMap(x -> userService.byEmail("test@user.com"))
		.as(tx::rollback)
		.as(StepVerifier::create)
		.verifyError(AccessDeniedException.class);
	}
	
	@Test
	void givenBadArgument_whenCreateUser_thenShouldThrowValidationException() {
		userService.create(UserCreateDto.builder()
				.password("12345678")
				.name("test")
				.build())
		.as(tx::rollback)
		.as(StepVerifier::create)
		.verifyError(ValidationException.class);

		userService.create(UserCreateDto.builder()
				.email("testuser.com")
				.password("12345678")
				.name("test")
				.build())
		.as(tx::rollback)
		.as(StepVerifier::create)
		.verifyError(ValidationException.class);

		userService.create(UserCreateDto.builder()
				.email("test@user.com")
				.name("test")
				.build())
		.as(tx::rollback)
		.as(StepVerifier::create)
		.verifyError(ValidationException.class);
	}
	
	@Test
	@WithMockUser(username = "user@user.com", roles = "ADMIN")
	void whenCorrectUser_thenShouldAddRole() {
		userService.create(UserCreateDto.builder()
				.email("test@user.com")
				.password("12345678")
				.name("test")
				.build())
		.flatMap(user -> userService.addRole(UserRoleDto.builder()
				.email(user.getEmail())
				.role("TEST")
				.build()))
		.as(tx::rollback)
		.as(StepVerifier::create)
		.assertNext(role -> {
			assertThat(role.getUser().getEmail()).isEqualTo("test@user.com");
			assertThat(role.getRole()).isEqualTo("TEST");
		})
		.verifyComplete();
	}
	
	@Test
	@WithMockUser(username = "test@user.com", roles = "ADMIN")
	void whenRemoveRole_thenShouldHaveEmpty() {
		userService.create(UserCreateDto.builder()
				.email("test@user.com")
				.password("12345678")
				.name("test")
				.build())
		.flatMap(user -> userService.removeRole(UserRoleDto.builder()
				.email(user.getEmail())
				.role("USER")
				.build()))
		.flatMapMany(user -> userService.getRoles(user))
		.buffer()
		.as(tx::rollback)
		.as(StepVerifier::create)
		.verifyComplete();
	}
	
	@Test
	@WithMockUser("test@user.com")
	void theInitialUserShouldHaveARoleUser() {
		userService.create(UserCreateDto.builder()
				.email("test@user.com")
				.password("12345678")
				.name("test")
				.build())
		.flatMapMany(user -> userService.getRoles(user))
		.as(tx::rollback)
		.as(StepVerifier::create)
		.expectNext("USER")
		.verifyComplete();
	}
	
	@Test
	void whenAnonymousUser_thenShouldThrowAccessDeniedException() {
		userService.create(UserCreateDto.builder()
				.email("test@user.com")
				.password("12345678")
				.name("test")
				.build())
		.flatMap(user -> userService.addRole(UserRoleDto.builder()
				.email(user.getEmail())
				.role("TEST")
				.build()))
		.as(tx::rollback)
		.as(StepVerifier::create)
		.verifyError(AccessDeniedException.class);
	}
	
	@Test
	@WithMockUser("user@user.com")
	void whenNoAdmin_thenShouldThrowAccessDeniedException() {
		userService.create(UserCreateDto.builder()
				.email("test@user.com")
				.password("12345678")
				.name("test")
				.build())
		.flatMap(user -> userService.addRole(UserRoleDto.builder()
				.email(user.getEmail())
				.role("TEST")
				.build()))
		.as(tx::rollback)
		.as(StepVerifier::create)
		.verifyError(AccessDeniedException.class);
	}
}
