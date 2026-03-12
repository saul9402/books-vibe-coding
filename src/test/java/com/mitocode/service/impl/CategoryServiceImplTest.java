package com.mitocode.service.impl;

import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.model.Category;
import com.mitocode.repo.ICategoryRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock ICategoryRepo repo;
    @InjectMocks CategoryServiceImpl service;

    @AfterEach
    void resetMocks() {
        Mockito.reset(repo);
    }

    @Test
    void findAll_emitsAllItems() {
        Category c1 = new Category("1", "Fiction", true);
        Category c2 = new Category("2", "Science", true);
        when(repo.findAll()).thenReturn(Flux.just(c1, c2));

        StepVerifier.create(service.findAll())
                .expectNext(c1, c2)
                .verifyComplete();
    }

    @Test
    void findAll_empty_completesWithoutItems() {
        when(repo.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(service.findAll())
                .verifyComplete();
    }

    @Test
    void findById_found_emitsSingle() {
        Category cat = new Category("1", "Fiction", true);
        when(repo.findById("1")).thenReturn(Mono.just(cat));

        StepVerifier.create(service.findById("1"))
                .expectNext(cat)
                .verifyComplete();
    }

    @Test
    void findById_notFound_emitsModelNotFoundException() {
        when(repo.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(service.findById("999"))
                .expectErrorMatches(ex ->
                        ex instanceof ModelNotFoundException &&
                        ex.getMessage().contains("999"))
                .verify();
    }

    @Test
    void save_returnsSavedEntity() {
        Category input = new Category(null, "Drama", true);
        Category saved = new Category("3", "Drama", true);
        when(repo.save(input)).thenReturn(Mono.just(saved));

        StepVerifier.create(service.save(input))
                .expectNext(saved)
                .verifyComplete();
    }

    @Test
    void update_found_savesAndReturnsUpdated() {
        Category existing = new Category("1", "Fiction", true);
        Category updated = new Category("1", "Updated", true);
        when(repo.findById("1")).thenReturn(Mono.just(existing));
        when(repo.save(any())).thenReturn(Mono.just(updated));

        StepVerifier.create(service.update("1", updated))
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    void update_notFound_emitsModelNotFoundException() {
        when(repo.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(service.update("999", new Category()))
                .expectError(ModelNotFoundException.class)
                .verify();
    }

    @Test
    void delete_found_completesEmpty() {
        Category cat = new Category("1", "Fiction", true);
        when(repo.findById("1")).thenReturn(Mono.just(cat));
        when(repo.deleteById("1")).thenReturn(Mono.empty());

        StepVerifier.create(service.delete("1"))
                .verifyComplete();
    }

    @Test
    void delete_notFound_emitsModelNotFoundException() {
        when(repo.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(service.delete("999"))
                .expectError(ModelNotFoundException.class)
                .verify();
    }
}

