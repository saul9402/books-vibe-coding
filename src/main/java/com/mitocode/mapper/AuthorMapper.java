package com.mitocode.mapper;

import com.mitocode.dto.AuthorDTO;
import com.mitocode.model.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorDTO toDto(Author author);
    Author toEntity(AuthorDTO dto);
}
