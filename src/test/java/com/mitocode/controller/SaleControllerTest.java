package com.mitocode.controller;

import com.mitocode.dto.*;
import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.mapper.SaleMapper;
import com.mitocode.model.*;
import com.mitocode.service.ISaleService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(SaleController.class)
class SaleControllerTest {

    @Autowired WebTestClient client;
    @MockitoBean ISaleService service;
    @MockitoBean SaleMapper saleMapper;

    @AfterEach
    void resetMocks() {
        Mockito.reset(service, saleMapper);
    }

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2024, 1, 15, 10, 0);

    private Sale sampleSale() {
        Client c = new Client("cli-1", "John", "Doe", LocalDate.of(1990, 1, 15));
        Book b = new Book("book-1", new Category("cat-1", "Fiction", true),
                "Clean Code", "978-0", "http://img.png", true);
        SaleDetail detail = new SaleDetail(b, 29.99, (short) 1, true);
        return new Sale("sale-1", c, FIXED_TIME, 29.99, true, List.of(detail));
    }

    private SaleDTO sampleDto() {
        ClientDTO clientDto = new ClientDTO("cli-1", "John", "Doe", LocalDate.of(1990, 1, 15));
        BookDTO bookDto = new BookDTO("book-1", "cat-1", "Clean Code", "978-0", "http://img.png", true);
        SaleDetailDTO detailDto = new SaleDetailDTO(null, bookDto, 29.99, (short) 1, true);
        return new SaleDTO("sale-1", clientDto, FIXED_TIME, 29.99, true, List.of(detailDto));
    }

    @Test
    void getAllSales_returnsOk() {
        when(service.findAll()).thenReturn(Flux.just(sampleSale()));
        when(saleMapper.toDto(any())).thenReturn(sampleDto());

        client.get().uri("/sales")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(200)
                .jsonPath("$.data.length()").isEqualTo(1);
    }

    @Test
    void getAllSales_empty_returnsEmptyList() {
        when(service.findAll()).thenReturn(Flux.empty());

        client.get().uri("/sales")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.data.length()").isEqualTo(0);
    }

    @Test
    void getSaleById_found_returnsOk() {
        when(service.findById("sale-1")).thenReturn(Mono.just(sampleSale()));
        when(saleMapper.toDto(any())).thenReturn(sampleDto());

        client.get().uri("/sales/sale-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.data[0].totalSale").isEqualTo(29.99);
    }

    @Test
    void getSaleById_notFound_returns404() {
        when(service.findById("999"))
                .thenReturn(Mono.error(new ModelNotFoundException("ID NOT FOUND: 999")));

        client.get().uri("/sales/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void saveSale_returnsCreated() {
        Sale saved = sampleSale();
        when(saleMapper.toEntity(any())).thenReturn(saved);
        when(service.save(any())).thenReturn(Mono.just(saved));

        client.post().uri("/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleDto())
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", ".*/sales/sale-1");
    }

    @Test
    void updateSale_returnsOk() {
        Sale updated = sampleSale();
        when(saleMapper.toEntity(any())).thenReturn(updated);
        when(service.update(eq("sale-1"), any())).thenReturn(Mono.just(updated));
        when(saleMapper.toDto(any())).thenReturn(sampleDto());

        client.put().uri("/sales/sale-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleDto())
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.data[0].totalSale").isEqualTo(29.99);
    }

    @Test
    void updateSale_notFound_returns404() {
        when(saleMapper.toEntity(any())).thenReturn(sampleSale());
        when(service.update(eq("999"), any()))
                .thenReturn(Mono.error(new ModelNotFoundException("ID NOT FOUND: 999")));

        client.put().uri("/sales/999")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleDto())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteSale_returnsNoContent() {
        when(service.delete("sale-1")).thenReturn(Mono.empty());

        client.delete().uri("/sales/sale-1")
                .exchange()
                .expectStatus().isNoContent();
    }
}

