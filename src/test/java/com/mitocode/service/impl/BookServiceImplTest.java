package com.mitocode.service.impl;

import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.model.Author;
import com.mitocode.model.Book;
import com.mitocode.model.Category;
import com.mitocode.repo.IBookRepo;
import com.mitocode.service.IAuthorService;
import com.mitocode.service.ICategoryService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock IBookRepo repo;
    @Mock IAuthorService authorService;
    @Mock ICategoryService categoryService;
    @InjectMocks BookServiceImpl service;

    @AfterEach
    void resetMocks() {
        Mockito.reset(repo, authorService, categoryService);
    }

    private Author sampleAuthor() {
        return new Author("author-1", "Gabriel", "Garcia Marquez", "Colombia");
    }

    private Category sampleCategory() {
        return new Category("cat-1", "Fiction", true);
    }

    private Book sampleBook() {
        return new Book("book-1", sampleCategory(), sampleAuthor(),
                "Clean Code", "978-0132350884", "http://img.png", true);
    }

    // ── findAll ────────────────────────────────────────────────────────────────

    @Test
    void findAll_emitsAllBooks() {
        Book b1 = sampleBook();
        Book b2 = new Book("book-2", sampleCategory(), sampleAuthor(),
                "Refactoring", "978-0201485677", "http://img2.png", true);
        when(repo.findAll()).thenReturn(Flux.just(b1, b2));

        StepVerifier.create(service.findAll())
                .expectNext(b1, b2)
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
        Book book = sampleBook();
        when(repo.findById("book-1")).thenReturn(Mono.just(book));

        StepVerifier.create(service.findById("book-1"))
                .expectNext(book)
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
        Book input = new Book(null, sampleCategory(), sampleAuthor(),
                "Clean Code", "978-0132350884", "http://img.png", true);
        Book saved = sampleBook();

        when(authorService.findById(anyString())).thenReturn(Mono.just(sampleAuthor()));
        when(categoryService.findById(anyString())).thenReturn(Mono.just(sampleCategory()));
        when(repo.save(any(Book.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(service.save(input))
                .expectNext(saved)
                .verifyComplete();
    }

    // ── update ─────────────────────────────────────────────────────────────────

    @Test
    void update_found_savesUpdated() {
        Book existing = sampleBook();
        Book updated = new Book("book-1", sampleCategory(), sampleAuthor(),
                "Clean Code v2", "978-0132350884", "http://img.png", true);

        when(repo.findById("book-1")).thenReturn(Mono.just(existing));
        when(authorService.findById(anyString())).thenReturn(Mono.just(sampleAuthor()));
        when(categoryService.findById(anyString())).thenReturn(Mono.just(sampleCategory()));
        when(repo.save(any())).thenReturn(Mono.just(updated));

        StepVerifier.create(service.update("book-1", updated))
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    void update_notFound_emitsModelNotFoundException() {
        when(repo.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(service.update("999", sampleBook()))
                .expectError(ModelNotFoundException.class)
                .verify();
    }

    // ── delete ─────────────────────────────────────────────────────────────────

    @Test
    void delete_found_completesEmpty() {
        Book book = sampleBook();
        when(repo.findById("book-1")).thenReturn(Mono.just(book));
        when(repo.deleteById("book-1")).thenReturn(Mono.empty());

        StepVerifier.create(service.delete("book-1"))
                .verifyComplete();
    }

    @Test
    void delete_notFound_emitsModelNotFoundException() {
        when(repo.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(service.delete("999"))
                .expectError(ModelNotFoundException.class)
                .verify();
    }

    // ── getBooksByCategory ─────────────────────────────────────────────────────

    @Test
    void getBooksByCategory_returnsMatchingBooks() {
        Book book = sampleBook();
        when(repo.getBooksByCategory("Fiction")).thenReturn(Flux.just(book));

        StepVerifier.create(service.getBooksByCategory("Fiction"))
                .expectNext(book)
                .verifyComplete();
    }

    @Test
    void getBooksByCategory_noMatch_completesEmpty() {
        when(repo.getBooksByCategory("Unknown")).thenReturn(Flux.empty());

        StepVerifier.create(service.getBooksByCategory("Unknown"))
                .verifyComplete();
    }
}
