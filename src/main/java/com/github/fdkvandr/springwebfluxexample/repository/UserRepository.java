package com.github.fdkvandr.springwebfluxexample.repository;

import com.github.fdkvandr.springwebfluxexample.domain.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Integer> {

    Mono<User> findByUsername(String username);
}
