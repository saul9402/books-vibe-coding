package com.mitocode.controller;

import com.mitocode.dto.AuthorDTO;
import com.mitocode.dto.GenericResponse;
import com.mitocode.mapper.AuthorMapper;
import com.mitocode.service.IAuthorService;
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
@RequestMapping("/authors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthorController {

    private final IAuthorService service;
    private final AuthorMapper authorMapper;

    @GetMapping
    public Mono<ResponseEntity<GenericResponse<AuthorDTO>>> getAllAuthors() {
        log.info("GET /authors");
        return service.findAll()
                .map(authorMapper::toDto)
                .collectList()
                .map(list -> ResponseEntity.ok(new GenericResponse<>(200, "success", list)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<GenericResponse<AuthorDTO>>> getAuthorById(@PathVariable String id) {
        log.info("GET /authors/{}", id);
        return service.findById(id)
                .map(obj -> ResponseEntity.ok(new GenericResponse<>(200, "success", List.of(authorMapper.toDto(obj)))));
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> save(@Valid @RequestBody AuthorDTO dto) {
        log.info("POST /authors");
        return service.save(authorMapper.toEntity(dto))
                .map(saved -> ResponseEntity.created(URI.create("/authors/" + saved.getIdAuthor())).build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<GenericResponse<AuthorDTO>>> update(@PathVariable String id, @Valid @RequestBody AuthorDTO dto) {
        log.info("PUT /authors/{}", id);
        return service.update(id, authorMapper.toEntity(dto))
                .map(obj -> ResponseEntity.ok(new GenericResponse<>(200, "success", List.of(authorMapper.toDto(obj)))));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        log.info("DELETE /authors/{}", id);
        return service.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
