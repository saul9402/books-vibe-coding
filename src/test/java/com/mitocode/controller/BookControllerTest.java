package com.mitocode.controller;

import com.mitocode.dto.BookDTO;
import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.mapper.BookMapper;
import com.mitocode.model.Author;
import com.mitocode.model.Book;
import com.mitocode.model.Category;
import com.mitocode.service.IBookService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(BookController.class)
class BookControllerTest {

    @Autowired WebTestClient client;
    @MockitoBean IBookService service;
    @MockitoBean BookMapper bookMapper;

    @AfterEach
    void resetMocks() {
        Mockito.reset(service, bookMapper);
    }

    private Book sampleBook() {
        return new Book("book-1", new Category("cat-1", "Fiction", true),
                new Author("author-1", "Gabriel", "Garcia Marquez", "Colombia"),
                "Clean Code", "978-0132350884", "http://img.png", true);
    }

    private BookDTO sampleDto() {
        return new BookDTO("book-1", "cat-1", "author-1", "Clean Code", "978-0132350884", "http://img.png", true);
    }

    @Test
    void getAllBooks_returnsOk() {
        when(service.findAll()).thenReturn(Flux.just(sampleBook()));
        when(bookMapper.toDto(any())).thenReturn(sampleDto());

        client.get().uri("/books")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(200)
                .jsonPath("$.data[0].title").isEqualTo("Clean Code");
    }

    @Test
    void getAllBooks_empty_returnsOkWithEmptyList() {
        when(service.findAll()).thenReturn(Flux.empty());

        client.get().uri("/books")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.data.length()").isEqualTo(0);
    }

    @Test
    void getBookById_found_returnsOk() {
        when(service.findById("book-1")).thenReturn(Mono.just(sampleBook()));
        when(bookMapper.toDto(any())).thenReturn(sampleDto());

        client.get().uri("/books/book-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.data[0].title").isEqualTo("Clean Code");
    }

    @Test
    void getBookById_notFound_returns404() {
        when(service.findById("999"))
                .thenReturn(Mono.error(new ModelNotFoundException("ID NOT FOUND: 999")));

        client.get().uri("/books/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void saveBook_valid_returnsCreated() {
        Book saved = sampleBook();
        when(bookMapper.toEntity(any())).thenReturn(saved);
        when(service.save(any())).thenReturn(Mono.just(saved));

        client.post().uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleDto())
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", ".*/books/book-1");
    }

    @Test
    void saveBook_nullTitle_returns400() {
        // @NotNull on title triggers WebExchangeBindException → GlobalErrorHandler → 400
        BookDTO invalid = new BookDTO("book-1", "cat-1", "author-1", null, "978-0", "http://img.png", true);

        client.post().uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalid)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateBook_returnsOk() {
        Book updated = sampleBook();
        when(bookMapper.toEntity(any())).thenReturn(updated);
        when(service.update(eq("book-1"), any())).thenReturn(Mono.just(updated));
        when(bookMapper.toDto(any())).thenReturn(sampleDto());

        client.put().uri("/books/book-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleDto())
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.data[0].title").isEqualTo("Clean Code");
    }

    @Test
    void updateBook_notFound_returns404() {
        when(bookMapper.toEntity(any())).thenReturn(sampleBook());
        when(service.update(eq("999"), any()))
                .thenReturn(Mono.error(new ModelNotFoundException("ID NOT FOUND: 999")));

        client.put().uri("/books/999")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleDto())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteBook_returnsNoContent() {
        when(service.delete("book-1")).thenReturn(Mono.empty());

        client.delete().uri("/books/book-1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void getBooksByCategory_returnsMatchingBooks() {
        when(service.getBooksByCategory("Fiction")).thenReturn(Flux.just(sampleBook()));
        when(bookMapper.toDto(any())).thenReturn(sampleDto());

        client.get().uri("/books/byCategory?category=Fiction")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.data.length()").isEqualTo(1);
    }

    @Test
    void getAllBooks_serviceError_returns500() {
        // Covers GlobalErrorHandler.handleDefaultException
        when(service.findAll()).thenReturn(Flux.error(new RuntimeException("DB unavailable")));

        client.get().uri("/books")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.status").isEqualTo(500);
    }
}
