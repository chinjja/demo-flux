package com.chinjja.app;

import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Transaction {
	private final TransactionalOperator tx;
	
	public <T> Mono<T> rollback(Mono<T> mono) {
		return tx.execute(t -> {
			t.setRollbackOnly();
			return mono;
		}).next();
	}
	
	public <T> Flux<T> rollback(Flux<T> flux) {
		return tx.execute(t -> {
			t.setRollbackOnly();
			return flux;
		});
	}
	
	public <T> Mono<T> transactional(Mono<T> mono) {
		return tx.transactional(mono);
	}
	
	public <T> Flux<T> transactional(Flux<T> flux) {
		return tx.transactional(flux);
	}
}
