package com.chinjja.app.router;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.chinjja.app.domain.Post;
import com.chinjja.app.domain.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.val;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PostRouter {
	private final PostRepository postRepository;
	
	public RouterFunction<ServerResponse> router() {
		return route()
				.GET("/{id}", req -> {
					val id = Long.valueOf(req.pathVariable("id"));
					val post = postRepository.findById(id);
					return ok().body(post, Post.class);
				})
				.GET(req -> {
					val posts = postRepository.findAll();
					return ok().body(posts, Post.class);
				})
				.POST(req -> req.bodyToMono(Post.class)
						.switchIfEmpty(Mono.error(new IllegalArgumentException("empty request")))
						.flatMap(dto -> postRepository.save(dto))
						.flatMap(post -> created(null).bodyValue(post)))
				.build();
	}
}
