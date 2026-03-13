package com.mitocode.controller;

import com.mitocode.dto.ClientDTO;
import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.mapper.ClientMapper;
import com.mitocode.model.Client;
import com.mitocode.service.IClientService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(ClientController.class)
class ClientControllerTest {

    @Autowired WebTestClient client;
    @MockitoBean IClientService service;
    @MockitoBean ClientMapper clientMapper;

    @AfterEach
    void resetMocks() {
        Mockito.reset(service, clientMapper);
    }

    private Client sampleClient() {
        return new Client("cli-1", "John", "Doe", LocalDate.of(1990, 1, 15), 34);
    }

    private ClientDTO sampleDto() {
        return new ClientDTO("cli-1", "John", "Doe", LocalDate.of(1990, 1, 15), 34);
    }

    @Test
    void getAllClients_returnsOk() {
        when(service.findAll()).thenReturn(Flux.just(sampleClient()));
        when(clientMapper.toDto(any())).thenReturn(sampleDto());

        client.get().uri("/clients")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(200)
                .jsonPath("$.data[0].firstName").isEqualTo("John");
    }

    @Test
    void getAllClients_empty_returnsEmptyList() {
        when(service.findAll()).thenReturn(Flux.empty());

        client.get().uri("/clients")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.data.length()").isEqualTo(0);
    }

    @Test
    void getClientById_found_returnsOk() {
        when(service.findById("cli-1")).thenReturn(Mono.just(sampleClient()));
        when(clientMapper.toDto(any())).thenReturn(sampleDto());

        client.get().uri("/clients/cli-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.data[0].firstName").isEqualTo("John");
    }

    @Test
    void getClientById_notFound_returns404() {
        when(service.findById("999"))
                .thenReturn(Mono.error(new ModelNotFoundException("ID NOT FOUND: 999")));

        client.get().uri("/clients/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void saveClient_valid_returnsCreated() {
        Client saved = sampleClient();
        when(clientMapper.toEntity(any())).thenReturn(saved);
        when(service.save(any())).thenReturn(Mono.just(saved));

        client.post().uri("/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleDto())
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", ".*/clients/cli-1");
    }

    @Test
    void saveClient_invalidFirstName_returns400() {
        // "Jo" has length 2 → violates @Size(min = 3) → WebExchangeBindException → 400
        ClientDTO invalid = new ClientDTO(null, "Jo", "Doe", LocalDate.of(1990, 1, 15), null);

        client.post().uri("/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalid)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateClient_returnsOk() {
        Client updated = sampleClient();
        ClientDTO updatedDto = new ClientDTO("cli-1", "Jane", "Doe", LocalDate.of(1990, 1, 15), 34);
        when(clientMapper.toEntity(any())).thenReturn(updated);
        when(service.update(eq("cli-1"), any())).thenReturn(Mono.just(updated));
        when(clientMapper.toDto(any())).thenReturn(updatedDto);

        client.put().uri("/clients/cli-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.data[0].firstName").isEqualTo("Jane");
    }

    @Test
    void updateClient_notFound_returns404() {
        when(clientMapper.toEntity(any())).thenReturn(sampleClient());
        when(service.update(eq("999"), any()))
                .thenReturn(Mono.error(new ModelNotFoundException("ID NOT FOUND: 999")));

        client.put().uri("/clients/999")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleDto())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteClient_returnsNoContent() {
        when(service.delete("cli-1")).thenReturn(Mono.empty());

        client.delete().uri("/clients/cli-1")
                .exchange()
                .expectStatus().isNoContent();
    }
}

