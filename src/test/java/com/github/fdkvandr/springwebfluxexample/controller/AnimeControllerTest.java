package com.github.fdkvandr.springwebfluxexample.controller;

import com.github.fdkvandr.springwebfluxexample.domain.Anime;
import com.github.fdkvandr.springwebfluxexample.service.AnimeService;
import com.github.fdkvandr.springwebfluxexample.util.AnimeCreator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

    @InjectMocks
    private AnimeController animeController;

    @Mock
    private AnimeService animeService;

    private final Anime anime = AnimeCreator.createAnimeToBeSaved();
    private final Anime validAnime = AnimeCreator.createValidAnime();
    private final Anime updatedAnime = AnimeCreator.createValidUpdatedAnime();

    @BeforeAll
    public static void blockHoundSetup() {
        BlockHound.install();
    }

    @BeforeEach
    public void setUp() {
        BDDMockito.when(animeService.findAll())
                .thenReturn(Flux.just(validAnime));
        BDDMockito.when(animeService.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(validAnime));
        BDDMockito.when(animeService.save(anime))
                .thenReturn(Mono.just(validAnime));
        BDDMockito.when(animeService.batchSave(List.of(anime, anime)))
                .thenReturn(Flux.just(validAnime, validAnime));
        BDDMockito.when(animeService.delete(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());
        BDDMockito.when(animeService.update(updatedAnime))
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
        StepVerifier.create(animeController.findAll())
                .expectSubscription()
                .expectNext(validAnime)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById returns a Mono with anime when it exists")
    void findById_ReturnMonoOfAnime_WhenSuccessful() {
        StepVerifier.create(animeController.findById(1))
                .expectSubscription()
                .expectNext(validAnime)
                .verifyComplete();
    }

    @Test
    @DisplayName("save creates an anime when successful")
    void save_CreatesAnime_WhenSuccessful() {
        StepVerifier.create(animeController.save(anime))
                .expectSubscription()
                .expectNext(validAnime)
                .verifyComplete();
    }

    @Test
    @DisplayName("batchSave creates list of anime when successful")
    void batchSave_CreatesListOfAnimeAnime_WhenSuccessful() {
        StepVerifier.create(animeController.batchSave(List.of(anime, anime)))
                .expectSubscription()
                .expectNext(validAnime, validAnime)
                .verifyComplete();
    }

    @Test
    @DisplayName("delete removes the anime when successful")
    void delete_RemovesTheAnime_WhenSuccessful() {
        StepVerifier.create(animeController.delete(1))
                .expectSubscription()
                .verifyComplete();
    }


    @Test
    @DisplayName("update save updated anime and returns empty Mono when successful")
    void update_SaveUpdatedAnime_WhenSuccessful() {
        StepVerifier.create(animeController.update(1, updatedAnime))
                .expectSubscription()
                .verifyComplete();
    }
}