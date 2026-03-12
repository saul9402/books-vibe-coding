package com.mitocode.mapper;

import com.mitocode.dto.BookDTO;
import com.mitocode.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    // Extract nested category ID to flat DTO field
    @Mapping(source = "category.idCategory", target = "idCategory")
    BookDTO toDto(Book book);

    // Reconstruct the embedded Category with only the ID set (name/status loaded by service if needed)
    @Mapping(source = "idCategory", target = "category.idCategory")
    @Mapping(target = "category.name", ignore = true)
    @Mapping(target = "category.status", ignore = true)
    Book toEntity(BookDTO dto);
}

