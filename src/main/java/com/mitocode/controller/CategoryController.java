package com.mitocode.controller;

import com.mitocode.dto.CategoryDTO;
import com.mitocode.dto.GenericResponse;
import com.mitocode.model.Category;
import com.mitocode.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {

    private final ICategoryService service;
    @Qualifier("defaultMapper")
    private final ModelMapper modelMapper;


    @GetMapping
    public ResponseEntity<GenericResponse<CategoryDTO>> getAllCategories() {        
        List<CategoryDTO> list = service.findAll().stream().map(this::convertToDto).toList();

        return ResponseEntity.ok(new GenericResponse<>(200, "success", list));
    }


    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<CategoryDTO>> getCategoryById(@PathVariable("id") Integer id) {
        Category obj = service.findById(id);

        return ResponseEntity.ok(new GenericResponse<>(200, "success", Arrays.asList(convertToDto(obj))));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CategoryDTO dto) {
        Category obj = service.save(convertToEntity(dto));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdCategory()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<CategoryDTO>> update(@PathVariable("id") Integer id, @RequestBody CategoryDTO dto) {
        //category.setIdCategory(id);
        Category obj = service.update(id, convertToEntity(dto));

        return ResponseEntity.ok(new GenericResponse<>(200, "success", Arrays.asList(convertToDto(obj))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }


    private CategoryDTO convertToDto(Category obj) {
        return modelMapper.map(obj, CategoryDTO.class);
    }

    private Category convertToEntity(CategoryDTO dto) {
        return modelMapper.map(dto, Category.class);
    }

}
