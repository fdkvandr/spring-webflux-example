package com.github.fdkvandr.springwebfluxexample.service;

import com.github.fdkvandr.springwebfluxexample.domain.Anime;
import com.github.fdkvandr.springwebfluxexample.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository animeRepository;

    public Flux<Anime> findAll() {
        return animeRepository.findAll();
    }
}
