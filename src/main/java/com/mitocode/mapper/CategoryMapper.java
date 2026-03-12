package com.mitocode.mapper;

import com.mitocode.dto.CategoryDTO;
import com.mitocode.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Category.name → CategoryDTO.categoryName
    @Mapping(source = "name", target = "categoryName")
    CategoryDTO toDto(Category category);

    @Mapping(source = "categoryName", target = "name")
    Category toEntity(CategoryDTO dto);
}

