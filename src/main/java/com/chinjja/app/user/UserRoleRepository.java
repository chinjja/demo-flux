package com.chinjja.app.user;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRoleRepository extends ReactiveCrudRepository<UserRole, Long> {
	Flux<String> findAllRoleByUser(User user);
	Mono<UserRole> findByUserAndRole(User user, String role);
	Mono<Integer> deleteByUserAndRole(User uesr, String role);
}
