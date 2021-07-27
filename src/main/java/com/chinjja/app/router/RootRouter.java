package com.chinjja.app.router;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.web.reactive.function.server.RouterFunction;

@Configuration
@RequiredArgsConstructor
public class RootRouter {
	private final UserRouter user;
	
	@Bean
	public RouterFunction<ServerResponse> router() {
		return route()
				.path("/api", api -> api
						.path("/users", () -> user.router())
						)
				.build();
	}
}
