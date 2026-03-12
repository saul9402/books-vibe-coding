package com.mitocode.controller;

import com.mitocode.dto.BookDTO;
import com.mitocode.dto.GenericResponse;
import com.mitocode.model.Book;
import com.mitocode.service.IBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookController {

    private final IBookService service;

    @Qualifier("defaultMapper")
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<GenericResponse<BookDTO>> getAllBooks() {
        List<BookDTO> list = service.findAll().stream().map(this::convertToDto).toList();

        return ResponseEntity.ok(new GenericResponse<>(200, "success", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<BookDTO>> getBookById(@PathVariable("id") Integer id) {
        Book obj = service.findById(id);

        return ResponseEntity.ok(new GenericResponse<>(200, "success", Arrays.asList(convertToDto(obj))));
    }

    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody BookDTO dto) {
        Book obj = service.save(convertToEntity(dto));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdBook()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<BookDTO>> update(@PathVariable("id") Integer id,@Valid @RequestBody BookDTO dto) {
        //client.setIdBook(id);
        Book obj = service.update(id, convertToEntity(dto));

        return ResponseEntity.ok(new GenericResponse<>(200, "success", Arrays.asList(convertToDto(obj))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/byCategory")
    public ResponseEntity<GenericResponse<BookDTO>> getBooksByCategory(@RequestParam("category") String category) {
        List<BookDTO> books = service.getBooksByCategory(category).stream().map(this::convertToDto).toList();

        return ResponseEntity.ok(new GenericResponse<>(200, "success", books));
    }

    private BookDTO convertToDto(Book obj) {
        return modelMapper.map(obj, BookDTO.class);
    }

    private Book convertToEntity(BookDTO dto) {
        return modelMapper.map(dto, Book.class);
    }

}
