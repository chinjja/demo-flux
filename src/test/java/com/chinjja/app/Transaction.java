package com.chinjja.app;

import org.springframework.stereotype.Component;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class Transaction {
	private static ReactiveTransactionManager tm;
	public Transaction(ReactiveTransactionManager tm) {
		Transaction.tm = tm;
	}
	
	public static <T> Mono<T> rollback(Mono<T> mono) {
		return TransactionalOperator.create(tm).execute(t -> {
			t.setRollbackOnly();
			return mono;
		}).next();
	}
	
	public static <T> Flux<T> rollback(Flux<T> flux) {
		return TransactionalOperator.create(tm).execute(t -> {
			t.setRollbackOnly();
			return flux;
		});
	}
}
