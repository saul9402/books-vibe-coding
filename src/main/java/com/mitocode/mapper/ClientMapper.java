package com.mitocode.mapper;

import com.mitocode.dto.ClientDTO;
import com.mitocode.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    // Client.primaryName → ClientDTO.firstName, Client.lastName → ClientDTO.surname
    @Mapping(source = "primaryName", target = "firstName")
    @Mapping(source = "lastName",    target = "surname")
    @Mapping(source = "birthDate",   target = "birthDateClient")
    ClientDTO toDto(Client client);

    @Mapping(source = "firstName",      target = "primaryName")
    @Mapping(source = "surname",        target = "lastName")
    @Mapping(source = "birthDateClient", target = "birthDate")
    Client toEntity(ClientDTO dto);
}

