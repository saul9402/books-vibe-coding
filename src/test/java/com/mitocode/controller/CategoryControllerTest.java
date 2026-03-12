package com.mitocode.controller;

import com.mitocode.dto.CategoryDTO;
import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.mapper.CategoryMapper;
import com.mitocode.model.Category;
import com.mitocode.service.ICategoryService;
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

@WebFluxTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired WebTestClient client;
    @MockitoBean ICategoryService service;
    @MockitoBean CategoryMapper categoryMapper;

    @AfterEach
    void resetMocks() {
        Mockito.reset(service, categoryMapper);
    }

    private Category sampleCategory() {
        return new Category("cat-1", "Fiction", true);
    }

    private CategoryDTO sampleDto() {
        return new CategoryDTO("cat-1", "Fiction", true);
    }

    @Test
    void getAllCategories_returnsOk() {
        when(service.findAll()).thenReturn(Flux.just(sampleCategory()));
        when(categoryMapper.toDto(any())).thenReturn(sampleDto());

        client.get().uri("/categories")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(200)
                .jsonPath("$.data[0].categoryName").isEqualTo("Fiction");
    }

    @Test
    void getAllCategories_empty_returnsEmptyList() {
        when(service.findAll()).thenReturn(Flux.empty());

        client.get().uri("/categories")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.data.length()").isEqualTo(0);
    }

    @Test
    void getCategoryById_found_returnsOk() {
        when(service.findById("cat-1")).thenReturn(Mono.just(sampleCategory()));
        when(categoryMapper.toDto(any())).thenReturn(sampleDto());

        client.get().uri("/categories/cat-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.data[0].categoryName").isEqualTo("Fiction");
    }

    @Test
    void getCategoryById_notFound_returns404() {
        when(service.findById("999"))
                .thenReturn(Mono.error(new ModelNotFoundException("ID NOT FOUND: 999")));

        client.get().uri("/categories/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void saveCategory_returnsCreated() {
        Category saved = sampleCategory();
        when(categoryMapper.toEntity(any())).thenReturn(saved);
        when(service.save(any())).thenReturn(Mono.just(saved));

        client.post().uri("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleDto())
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", ".*/categories/cat-1");
    }

    @Test
    void updateCategory_returnsOk() {
        Category updated = sampleCategory();
        CategoryDTO updatedDto = new CategoryDTO("cat-1", "Updated", true);
        when(categoryMapper.toEntity(any())).thenReturn(updated);
        when(service.update(eq("cat-1"), any())).thenReturn(Mono.just(updated));
        when(categoryMapper.toDto(any())).thenReturn(updatedDto);

        client.put().uri("/categories/cat-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.data[0].categoryName").isEqualTo("Updated");
    }

    @Test
    void updateCategory_notFound_returns404() {
        when(categoryMapper.toEntity(any())).thenReturn(sampleCategory());
        when(service.update(eq("999"), any()))
                .thenReturn(Mono.error(new ModelNotFoundException("ID NOT FOUND: 999")));

        client.put().uri("/categories/999")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleDto())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteCategory_returnsNoContent() {
        when(service.delete("cat-1")).thenReturn(Mono.empty());

        client.delete().uri("/categories/cat-1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
