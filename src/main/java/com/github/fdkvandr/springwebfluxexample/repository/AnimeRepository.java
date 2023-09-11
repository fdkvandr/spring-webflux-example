package com.github.fdkvandr.springwebfluxexample.repository;

import com.github.fdkvandr.springwebfluxexample.domain.Anime;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AnimeRepository extends ReactiveCrudRepository<Anime, Integer> {

}
