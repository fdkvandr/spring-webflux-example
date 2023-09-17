package com.github.fdkvandr.springwebfluxexample.service;

import com.github.fdkvandr.springwebfluxexample.domain.Anime;
import com.github.fdkvandr.springwebfluxexample.repository.AnimeRepository;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository animeRepository;

    public Flux<Anime> findAll() {
        return animeRepository.findAll();
    }

    public Mono<Anime> findById(int id) {
        return animeRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found")));
    }

    public Mono<Anime> save(Anime anime) {
        return animeRepository.save(anime);
    }

    @Transactional
    public Flux<Anime> batchSave(List<Anime> animes) {
        return animeRepository.saveAll(animes)
                .doOnNext(it -> {
                    if (StringUtil.isNullOrEmpty(it.getName()))
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid name");
                });
    }

    public Mono<Void> update(Anime anime) {
        return findById(anime.getId())
                .flatMap(animeRepository::save)
                .then();
    }

    public Mono<Void> delete(int id) {
        return findById(id)
                .flatMap(animeRepository::delete);
    }
}
