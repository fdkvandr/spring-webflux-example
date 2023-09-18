package com.github.fdkvandr.springwebfluxexample.integration;

import com.github.fdkvandr.springwebfluxexample.domain.Anime;
import com.github.fdkvandr.springwebfluxexample.repository.AnimeRepository;
import com.github.fdkvandr.springwebfluxexample.util.AnimeCreator;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AnimeControllerIT {

    @MockBean
    private AnimeRepository animeRepository;

    @Autowired
    private WebTestClient testClient;

    private final Anime anime = AnimeCreator.createAnimeToBeSaved();
    private final Anime validAnime = AnimeCreator.createValidAnime();
    private final Anime updatedAnime = AnimeCreator.createValidUpdatedAnime();

    @BeforeAll
    public static void blockHoundSetup() {
        BlockHound.install();
    }

    @BeforeEach
    public void setUp() {
        BDDMockito.when(animeRepository.findAll())
                .thenReturn(Flux.just(validAnime));
        BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(validAnime));
        BDDMockito.when(animeRepository.save(anime))
                .thenReturn(Mono.just(validAnime));
        BDDMockito.when(animeRepository.delete(ArgumentMatchers.any(Anime.class)))
                .thenReturn(Mono.empty());
    }

    @Test
    public void checkBlockHoundWorks() {
        try {
            FutureTask<?> task = new FutureTask<>(() -> {
                Thread.sleep(0);
                return "";
            });
            Schedulers.parallel().schedule(task);

            task.get(10, TimeUnit.SECONDS);
            Assertions.fail("should fail");
        } catch (Exception e) {
            Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
        }
    }

    @Test
    @DisplayName("findAll returns a flux of anime")
    void findAll_ReturnFluxOfAnime_WhenSuccessful() {
        testClient.get()
                .uri("/animes")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.[0].id").isEqualTo(validAnime.getId())
                             .jsonPath("$.[0].name").isEqualTo(validAnime.getName());
    }

    @Test
    @DisplayName("findAll returns a flux of anime")
    void findAll_ReturnFluxOfAnime_WhenSuccessful_v2() {
        testClient.get()
                .uri("/animes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Anime.class)
                .hasSize(1)
                .contains(validAnime);
    }

    @Test
    @DisplayName("findById returns a Mono with anime when it exists")
    void findById_ReturnMonoOfAnime_WhenSuccessful() {
        testClient.get()
                .uri("/animes/{id}", Map.of("id", 1))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Anime.class)
                .isEqualTo(validAnime);
    }

    @Test
    @DisplayName("findById returns a Mono еггое when anime does not exist")
    void findById_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());
        testClient.get()
                .uri("/animes/{id}", 1)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.status").isEqualTo(HttpStatus.NOT_FOUND.value())
                             .jsonPath("$.error").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                             .jsonPath("$.message").isEqualTo("404 NOT_FOUND \"Anime not found\"");
    }

    @Test
    @DisplayName("save creates an anime when successful")
    void save_CreatesAnime_WhenSuccessful() {
        testClient.post()
                .uri("/animes")
                .contentType(MediaType.APPLICATION_JSON)
                // .bodyValue(anime)
                .body(BodyInserters.fromValue(anime))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Anime.class)
                .isEqualTo(validAnime);
    }

    @Test
    @DisplayName("save returns Mono error with bad request when name is empty")
    void save_ReturnsError_WhenNameIsEmpty() {
        testClient.post()
                .uri("/animes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(anime.withName("")))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.status").isEqualTo(HttpStatus.BAD_REQUEST.value())
                             .jsonPath("$.error").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());

    }

    @Test
    @DisplayName("batchSave creates list of anime when successful")
    void batchSave_CreatesListOfAnimeAnime_WhenSuccessful() {
        testClient.post()
                .uri("/animes/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(List.of(anime, anime)))
                .exchange()
                .expectStatus().isCreated()
                .expectBodyList(Anime.class)
                .hasSize(2)
                .contains(validAnime, validAnime);
    }

    @Test
    @Disabled
    @DisplayName("batchSave returns Mono error when one of the animes in the list contains empty or null name")
    void batchSave_ReturnsMonoError_WhenContainsInvalidName() {
        testClient.post()
                .uri("/animes/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(List.of(anime, anime.withName(""))))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.status").isEqualTo(HttpStatus.BAD_REQUEST.value())
                             .jsonPath("$.error").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    }

    @Test
    @DisplayName("delete removes the anime when successful")
    void delete_RemovesTheAnime_WhenSuccessful() {
        testClient.delete()
                .uri("/animes/{id}", 1)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    @DisplayName("delete returns Mono error when anime does not exist")
    void delete_ReturnsMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());
        testClient.delete()
                .uri("/animes/{id}", 1)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.status").isEqualTo(HttpStatus.NOT_FOUND.value())
                             .jsonPath("$.error").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                             .jsonPath("$.message").isEqualTo("404 NOT_FOUND \"Anime not found\"");
    }

    @Test
    @DisplayName("update save updated anime and returns empty Mono when successful")
    void update_SaveUpdatedAnime_WhenSuccessful() {
        BDDMockito.when(animeRepository.save(updatedAnime))
                .thenReturn(Mono.just(updatedAnime));
        testClient.put()
                .uri("/animes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(updatedAnime))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    @DisplayName("update returns Mono error with bad request when name is empty")
    void update_ReturnsError_WhenNameIsEmpty() {
        testClient.put()
                .uri("/animes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(updatedAnime.withName("")))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.status").isEqualTo(HttpStatus.BAD_REQUEST.value())
                             .jsonPath("$.error").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    }

    @Test
    @DisplayName("update returns Mono error when anime does not exist")
    void update_ReturnsMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());
        testClient.put()
                .uri("/animes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(updatedAnime))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.status").isEqualTo(HttpStatus.NOT_FOUND.value())
                             .jsonPath("$.error").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                             .jsonPath("$.message").isEqualTo("404 NOT_FOUND \"Anime not found\"");
    }
}
