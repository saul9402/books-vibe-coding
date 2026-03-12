package com.mitocode.controller;

import com.mitocode.dto.ClientDTO;
import com.mitocode.dto.GenericResponse;
import com.mitocode.mapper.ClientMapper;
import com.mitocode.service.IClientService;
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
@RequestMapping("/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClientController {

    private final IClientService service;
    private final ClientMapper clientMapper;

    @GetMapping
    public Mono<ResponseEntity<GenericResponse<ClientDTO>>> getAllClients() {
        log.info("GET /clients");
        return service.findAll()
                .map(clientMapper::toDto)
                .collectList()
                .map(list -> ResponseEntity.ok(new GenericResponse<>(200, "success", list)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<GenericResponse<ClientDTO>>> getClientById(@PathVariable String id) {
        log.info("GET /clients/{}", id);
        return service.findById(id)
                .map(obj -> ResponseEntity.ok(new GenericResponse<>(200, "success", List.of(clientMapper.toDto(obj)))));
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> save(@Valid @RequestBody ClientDTO dto) {
        log.info("POST /clients");
        return service.save(clientMapper.toEntity(dto))
                .map(saved -> ResponseEntity.created(URI.create("/clients/" + saved.getIdClient())).build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<GenericResponse<ClientDTO>>> update(@PathVariable String id, @Valid @RequestBody ClientDTO dto) {
        log.info("PUT /clients/{}", id);
        return service.update(id, clientMapper.toEntity(dto))
                .map(obj -> ResponseEntity.ok(new GenericResponse<>(200, "success", List.of(clientMapper.toDto(obj)))));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        log.info("DELETE /clients/{}", id);
        return service.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
