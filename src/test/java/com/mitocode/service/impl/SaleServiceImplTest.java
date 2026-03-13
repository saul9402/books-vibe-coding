package com.mitocode.service.impl;

import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.model.*;
import com.mitocode.repo.ISaleRepo;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleServiceImplTest {

    @Mock ISaleRepo repo;
    @InjectMocks SaleServiceImpl service;

    @AfterEach
    void resetMocks() {
        Mockito.reset(repo);
    }

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2024, 1, 15, 10, 0);

    private Sale sampleSale() {
        Client c = new Client("cli-1", "John", "Doe", LocalDate.of(1990, 1, 15), 34);
        Book b = new Book("book-1", new Category("cat-1", "Fiction", true),
                new Author("author-1", "Gabriel", "Garcia Marquez", "Colombia"),
                "Clean Code", "978-0132350884", "http://img.png", true);
        SaleDetail detail = new SaleDetail(b, 29.99, (short) 1, true);
        return new Sale("sale-1", c, FIXED_TIME, 29.99, true, List.of(detail));
    }

    // ── findAll ────────────────────────────────────────────────────────────────

    @Test
    void findAll_emitsAllSales() {
        Sale s1 = sampleSale();
        when(repo.findAll()).thenReturn(Flux.just(s1));

        StepVerifier.create(service.findAll())
                .expectNext(s1)
                .verifyComplete();
    }

    @Test
    void findAll_empty_completesWithoutItems() {
        when(repo.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(service.findAll())
                .verifyComplete();
    }

    // ── findById ───────────────────────────────────────────────────────────────

    @Test
    void findById_found_emitsSingle() {
        Sale sale = sampleSale();
        when(repo.findById("sale-1")).thenReturn(Mono.just(sale));

        StepVerifier.create(service.findById("sale-1"))
                .expectNext(sale)
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

    // ── save ───────────────────────────────────────────────────────────────────

    @Test
    void save_returnsSavedEntity() {
        Sale input = sampleSale();
        input.setIdSale(null);
        Sale saved = sampleSale();
        when(repo.save(input)).thenReturn(Mono.just(saved));

        StepVerifier.create(service.save(input))
                .expectNext(saved)
                .verifyComplete();
    }

    // ── update ─────────────────────────────────────────────────────────────────

    @Test
    void update_found_savesUpdated() {
        Sale existing = sampleSale();
        Sale updated = sampleSale();
        updated.setTotal(59.99);
        when(repo.findById("sale-1")).thenReturn(Mono.just(existing));
        when(repo.save(any())).thenReturn(Mono.just(updated));

        StepVerifier.create(service.update("sale-1", updated))
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    void update_notFound_emitsModelNotFoundException() {
        when(repo.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(service.update("999", sampleSale()))
                .expectError(ModelNotFoundException.class)
                .verify();
    }

    // ── delete ─────────────────────────────────────────────────────────────────

    @Test
    void delete_found_completesEmpty() {
        Sale sale = sampleSale();
        when(repo.findById("sale-1")).thenReturn(Mono.just(sale));
        when(repo.deleteById("sale-1")).thenReturn(Mono.empty());

        StepVerifier.create(service.delete("sale-1"))
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
