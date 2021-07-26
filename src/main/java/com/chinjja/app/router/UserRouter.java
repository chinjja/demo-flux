package com.chinjja.app.router;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import javax.validation.ValidationException;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.chinjja.app.user.User;
import com.chinjja.app.user.UserService;
import com.chinjja.app.user.dto.UserCreateDto;
import com.chinjja.app.user.dto.UserUpdateDto;

import lombok.RequiredArgsConstructor;
import lombok.val;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserRouter {
	private final UserService userService;
	
	public RouterFunction<ServerResponse> router() {
		return route()
				.GET("/search/by-email", req -> {
					val email = req.queryParam("email").get();
					return ok().body(userService.byEmail(email), User.class);
				})
				.GET("/{id}", req -> {
					val id = Long.valueOf(req.pathVariable("id"));
					return ok().body(userService.byId(id), User.class);
				})
				.PATCH("/{id}", req -> {
					val id = Long.valueOf(req.pathVariable("id"));
					return Mono.zip(userService.byId(id), req.bodyToMono(UserUpdateDto.class))
							.flatMap(t -> userService.update(t.getT1(), t.getT2()))
							.flatMap(user -> ok().bodyValue(user))
							.switchIfEmpty(badRequest().bodyValue("empty request data"))
							.onErrorResume(ValidationException.class, e -> badRequest().bodyValue(e.getMessage()));
				})
				.POST(req -> req.bodyToMono(UserCreateDto.class)
						.flatMap(dto -> userService.create(dto))
						.flatMap(user -> created(null).bodyValue(user))
						.switchIfEmpty(badRequest().bodyValue("empty request data"))
						.onErrorResume(ValidationException.class, e -> badRequest().bodyValue(e.getMessage())))
				.build();
	}
}
