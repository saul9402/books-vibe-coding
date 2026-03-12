package com.mitocode.mapper;

import com.mitocode.dto.BookDTO;
import com.mitocode.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    // Extract nested category and author IDs to flat DTO fields
    @Mapping(source = "category.idCategory", target = "idCategory")
    @Mapping(source = "author.idAuthor", target = "idAuthor")
    BookDTO toDto(Book book);

    // Reconstruct the embedded Category and Author with only the IDs set
    @Mapping(source = "idCategory", target = "category.idCategory")
    @Mapping(target = "category.name", ignore = true)
    @Mapping(target = "category.status", ignore = true)
    @Mapping(source = "idAuthor", target = "author.idAuthor")
    @Mapping(target = "author.firstName", ignore = true)
    @Mapping(target = "author.lastName", ignore = true)
    @Mapping(target = "author.country", ignore = true)
    Book toEntity(BookDTO dto);
}
