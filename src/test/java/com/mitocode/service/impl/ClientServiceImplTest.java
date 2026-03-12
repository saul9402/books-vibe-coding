package com.mitocode.service.impl;

import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.model.Client;
import com.mitocode.repo.IClientRepo;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock IClientRepo repo;
    @InjectMocks ClientServiceImpl service;

    @AfterEach
    void resetMocks() {
        Mockito.reset(repo);
    }

    private Client sampleClient() {
        return new Client("cli-1", "John", "Doe", LocalDate.of(1990, 1, 15));
    }

    // ── findAll ────────────────────────────────────────────────────────────────

    @Test
    void findAll_emitsAllClients() {
        Client c1 = sampleClient();
        Client c2 = new Client("cli-2", "Jane", "Smith", LocalDate.of(1992, 5, 20));
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

    // ── findById ───────────────────────────────────────────────────────────────

    @Test
    void findById_found_emitsSingle() {
        Client client = sampleClient();
        when(repo.findById("cli-1")).thenReturn(Mono.just(client));

        StepVerifier.create(service.findById("cli-1"))
                .expectNext(client)
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
        Client input = new Client(null, "John", "Doe", LocalDate.of(1990, 1, 15));
        Client saved = sampleClient();
        when(repo.save(input)).thenReturn(Mono.just(saved));

        StepVerifier.create(service.save(input))
                .expectNext(saved)
                .verifyComplete();
    }

    // ── update ─────────────────────────────────────────────────────────────────

    @Test
    void update_found_savesUpdated() {
        Client existing = sampleClient();
        Client updated = new Client("cli-1", "Johnny", "Doe", LocalDate.of(1990, 1, 15));
        when(repo.findById("cli-1")).thenReturn(Mono.just(existing));
        when(repo.save(any())).thenReturn(Mono.just(updated));

        StepVerifier.create(service.update("cli-1", updated))
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    void update_notFound_emitsModelNotFoundException() {
        when(repo.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(service.update("999", sampleClient()))
                .expectError(ModelNotFoundException.class)
                .verify();
    }

    // ── delete ─────────────────────────────────────────────────────────────────

    @Test
    void delete_found_completesEmpty() {
        Client client = sampleClient();
        when(repo.findById("cli-1")).thenReturn(Mono.just(client));
        when(repo.deleteById("cli-1")).thenReturn(Mono.empty());

        StepVerifier.create(service.delete("cli-1"))
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

