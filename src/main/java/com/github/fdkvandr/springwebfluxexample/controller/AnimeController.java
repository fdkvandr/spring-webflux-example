package com.github.fdkvandr.springwebfluxexample.controller;

import com.github.fdkvandr.springwebfluxexample.domain.Anime;
import com.github.fdkvandr.springwebfluxexample.service.AnimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/animes")
@RequiredArgsConstructor
public class AnimeController {

    private final AnimeService animeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "findAll animes", tags = {"anime"}, security = @SecurityRequirement(name = "Basic Authentication"))
    public Flux<Anime> findAll() {
        return animeService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "findById anime", tags = {"anime"}, security = @SecurityRequirement(name = "Basic Authentication"))
    public Mono<Anime> findById(@PathVariable("id") int id) {
        return animeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "save anime", tags = {"anime"}, security = @SecurityRequirement(name = "Basic Authentication"))
    public Mono<Anime> save(@Valid @RequestBody Anime anime) {
        return animeService.save(anime);
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "batchSave animes", tags = {"anime"}, security = @SecurityRequirement(name = "Basic Authentication"))
    public Flux<Anime> batchSave(@RequestBody List<Anime> animes) {
        return animeService.batchSave(animes);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "update anime", tags = {"anime"}, security = @SecurityRequirement(name = "Basic Authentication"))
    public Mono<Void> update(@PathVariable("id") int id, @Valid @RequestBody Anime anime) {
        return animeService.update(anime.withId(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "delete anime", tags = {"anime"}, security = @SecurityRequirement(name = "Basic Authentication"))
    public Mono<Void> delete(@PathVariable("id") int id) {
        return animeService.delete(id);
    }
}
