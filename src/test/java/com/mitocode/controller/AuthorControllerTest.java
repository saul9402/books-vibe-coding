package com.mitocode.controller;

import com.mitocode.dto.AuthorDTO;
import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.mapper.AuthorMapper;
import com.mitocode.model.Author;
import com.mitocode.service.IAuthorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired WebTestClient client;
    @MockitoBean IAuthorService service;
    @MockitoBean AuthorMapper authorMapper;

    @AfterEach
    void resetMocks() {
        Mockito.reset(service, authorMapper);
    }

    private Author sampleAuthor() {
        return new Author("author-1", "Gabriel", "Garcia Marquez", "Colombia");
    }

    private AuthorDTO sampleDto() {
        return new AuthorDTO("author-1", "Gabriel", "Garcia Marquez", "Colombia");
    }

    @Test
    void getAllAuthors_returnsOk() {
        when(service.findAll()).thenReturn(Flux.just(sampleAuthor()));
        when(authorMapper.toDto(any())).thenReturn(sampleDto());

        client.get().uri("/authors")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(200)
                .jsonPath("$.data[0].firstName").isEqualTo("Gabriel");
    }

    @Test
    void getAuthorById_found_returnsOk() {
        when(service.findById("author-1")).thenReturn(Mono.just(sampleAuthor()));
        when(authorMapper.toDto(any())).thenReturn(sampleDto());

        client.get().uri("/authors/author-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.data[0].firstName").isEqualTo("Gabriel");
    }

    @Test
    void getAuthorById_notFound_returns404() {
        when(service.findById("999"))
                .thenReturn(Mono.error(new ModelNotFoundException("ID NOT FOUND: 999")));

        client.get().uri("/authors/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void saveAuthor_valid_returnsCreated() {
        Author saved = sampleAuthor();
        when(authorMapper.toEntity(any())).thenReturn(saved);
        when(service.save(any())).thenReturn(Mono.just(saved));

        client.post().uri("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleDto())
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", ".*/authors/author-1");
    }

    @Test
    void updateAuthor_returnsOk() {
        Author updated = sampleAuthor();
        when(authorMapper.toEntity(any())).thenReturn(updated);
        when(service.update(eq("author-1"), any())).thenReturn(Mono.just(updated));
        when(authorMapper.toDto(any())).thenReturn(sampleDto());

        client.put().uri("/authors/author-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleDto())
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.data[0].firstName").isEqualTo("Gabriel");
    }

    @Test
    void deleteAuthor_returnsNoContent() {
        when(service.delete("author-1")).thenReturn(Mono.empty());

        client.delete().uri("/authors/author-1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
