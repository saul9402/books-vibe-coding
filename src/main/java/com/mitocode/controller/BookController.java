package com.mitocode.controller;

import com.mitocode.dto.BookDTO;
import com.mitocode.dto.GenericResponse;
import com.mitocode.mapper.BookMapper;
import com.mitocode.service.IBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookController {

    private final IBookService service;
    private final BookMapper bookMapper;

    @GetMapping
    public Mono<ResponseEntity<GenericResponse<BookDTO>>> getAllBooks() {
        log.info("GET /books");
        return service.findAll()
                .map(bookMapper::toDto)
                .collectList()
                .map(list -> ResponseEntity.ok(new GenericResponse<>(200, "success", list)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<GenericResponse<BookDTO>>> getBookById(@PathVariable String id) {
        log.info("GET /books/{}", id);
        return service.findById(id)
                .map(obj -> ResponseEntity.ok(new GenericResponse<>(200, "success", List.of(bookMapper.toDto(obj)))));
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> save(@Valid @RequestBody BookDTO dto) {
        log.info("POST /books");
        return service.save(bookMapper.toEntity(dto))
                .map(saved -> ResponseEntity.created(URI.create("/books/" + saved.getIdBook())).build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<GenericResponse<BookDTO>>> update(@PathVariable String id, @Valid @RequestBody BookDTO dto) {
        log.info("PUT /books/{}", id);
        return service.update(id, bookMapper.toEntity(dto))
                .map(obj -> ResponseEntity.ok(new GenericResponse<>(200, "success", List.of(bookMapper.toDto(obj)))));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        log.info("DELETE /books/{}", id);
        return service.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @GetMapping("/byCategory")
    public Mono<ResponseEntity<GenericResponse<BookDTO>>> getBooksByCategory(@RequestParam String category) {
        log.info("GET /books/byCategory?category={}", category);
        return service.getBooksByCategory(category)
                .map(bookMapper::toDto)
                .collectList()
                .map(list -> ResponseEntity.ok(new GenericResponse<>(200, "success", list)));
    }
}
