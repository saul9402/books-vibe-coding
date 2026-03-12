package com.mitocode.controller;

import com.mitocode.dto.CategoryDTO;
import com.mitocode.dto.GenericResponse;
import com.mitocode.mapper.CategoryMapper;
import com.mitocode.service.ICategoryService;
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
@RequestMapping("/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {

    private final ICategoryService service;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public Mono<ResponseEntity<GenericResponse<CategoryDTO>>> getAllCategories() {
        log.info("GET /categories");
        return service.findAll()
                .map(categoryMapper::toDto)
                .collectList()
                .map(list -> ResponseEntity.ok(new GenericResponse<>(200, "success", list)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<GenericResponse<CategoryDTO>>> getCategoryById(@PathVariable String id) {
        log.info("GET /categories/{}", id);
        return service.findById(id)
                .map(obj -> ResponseEntity.ok(new GenericResponse<>(200, "success", List.of(categoryMapper.toDto(obj)))));
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> save(@Valid @RequestBody CategoryDTO dto) {
        log.info("POST /categories");
        return service.save(categoryMapper.toEntity(dto))
                .map(saved -> ResponseEntity.created(URI.create("/categories/" + saved.getIdCategory())).build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<GenericResponse<CategoryDTO>>> update(@PathVariable String id, @Valid @RequestBody CategoryDTO dto) {
        log.info("PUT /categories/{}", id);
        return service.update(id, categoryMapper.toEntity(dto))
                .map(obj -> ResponseEntity.ok(new GenericResponse<>(200, "success", List.of(categoryMapper.toDto(obj)))));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        log.info("DELETE /categories/{}", id);
        return service.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
