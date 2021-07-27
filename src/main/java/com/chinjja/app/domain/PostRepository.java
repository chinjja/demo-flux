package com.chinjja.app.domain;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface PostRepository extends ReactiveSortingRepository<Post, Long> {

}
