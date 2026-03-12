package com.mitocode.controller;

import com.mitocode.dto.GenericResponse;
import com.mitocode.dto.SaleDTO;
import com.mitocode.mapper.SaleMapper;
import com.mitocode.service.ISaleService;
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
@RequestMapping("/sales")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SaleController {

    private final ISaleService service;
    private final SaleMapper saleMapper;

    @GetMapping
    public Mono<ResponseEntity<GenericResponse<SaleDTO>>> getAllSales() {
        log.info("GET /sales");
        return service.findAll()
                .map(saleMapper::toDto)
                .collectList()
                .map(list -> ResponseEntity.ok(new GenericResponse<>(200, "success", list)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<GenericResponse<SaleDTO>>> getSaleById(@PathVariable String id) {
        log.info("GET /sales/{}", id);
        return service.findById(id)
                .map(obj -> ResponseEntity.ok(new GenericResponse<>(200, "success", List.of(saleMapper.toDto(obj)))));
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> save(@Valid @RequestBody SaleDTO dto) {
        log.info("POST /sales");
        return service.save(saleMapper.toEntity(dto))
                .map(saved -> ResponseEntity.created(URI.create("/sales/" + saved.getIdSale())).build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<GenericResponse<SaleDTO>>> update(@PathVariable String id, @Valid @RequestBody SaleDTO dto) {
        log.info("PUT /sales/{}", id);
        return service.update(id, saleMapper.toEntity(dto))
                .map(obj -> ResponseEntity.ok(new GenericResponse<>(200, "success", List.of(saleMapper.toDto(obj)))));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        log.info("DELETE /sales/{}", id);
        return service.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
