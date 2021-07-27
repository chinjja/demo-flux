package com.chinjja.app.user;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.chinjja.app.user.dto.UserCreateDto;
import com.chinjja.app.user.dto.UserEmailDto;
import com.chinjja.app.user.dto.UserPasswordDto;
import com.chinjja.app.user.dto.UserRoleDto;
import com.chinjja.app.user.dto.UserUpdateDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Validated
@Transactional(readOnly = true)
public interface UserService {
	@PostAuthorize("isAuthenticated() and returnObject?.email == principal.username")
	Mono<User> byId(Long id);
	
	@PostAuthorize("isAuthenticated() and returnObject?.email == principal.username")
	Mono<User> byEmail(String email);
	
	Mono<Boolean> existsByEmail(String email);
	
	@Transactional
	Mono<User> create(@Valid UserCreateDto dto);
	
	@Transactional
	Mono<User> init();
	
	@Transactional
	@PreAuthorize("isAuthenticated() and #user.email == principal.username")
	Mono<User> update(User user, @Valid UserUpdateDto dto);
	
	@Transactional
	@PreAuthorize("isAuthenticated() and #user.email == principal.username")
	Mono<User> updateEmail(User user, @Valid UserEmailDto dto);
	
	@Transactional
	@PreAuthorize("isAuthenticated() and #user.email == principal.username")
	Mono<User> updatePassword(User user, @Valid UserPasswordDto dto);
	
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	Mono<UserRole> addRole(@Valid UserRoleDto dto);
	
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	Mono<User> removeRole(@Valid UserRoleDto dto);
	
	@PreAuthorize("isAuthenticated() and #user.email == principal.username")
	Flux<String> getRoles(User user);
}
