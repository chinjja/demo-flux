package com.chinjja.app.user.impl;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.chinjja.app.user.User;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class UserRoleRepositoryImpl {
	private final DatabaseClient client;
	
	public Flux<String> findAllRoleByUser(User user) {
		return client.sql("select user_role.role from user_role where user_role.user=:user")
		.bind("user", user.getId())
		.fetch()
		.all()
		.map(mapper -> mapper.get("role"))
		.cast(String.class);
	}
}
