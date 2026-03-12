package com.mitocode.config;

import com.mitocode.dto.ClientDTO;
import com.mitocode.dto.SaleDTO;
import com.mitocode.model.Client;
import com.mitocode.model.Sale;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean(name = "defaultMapper")
    public ModelMapper defaultMapper() {
        return new ModelMapper();
    }

    @Bean(name = "clientMapper")
    public ModelMapper clientMapper() {
        ModelMapper mapper = new ModelMapper();

        //Lectura GET
        mapper.createTypeMap(Client.class, ClientDTO.class)
                .addMapping(Client::getPrimaryName, (dest, v) -> dest.setFirstName((String) v)) //e -> e.getPrimaryName()
                .addMapping(Client::getLastName, (dest, v) -> dest.setSurname((String) v));

        //Escritura POST PUT
        mapper.createTypeMap(ClientDTO.class, Client.class)
                .addMapping(ClientDTO::getFirstName, (dest, v) -> dest.setPrimaryName((String) v))
                .addMapping(ClientDTO::getSurname, (dest, v) -> dest.setLastName((String) v));

        return mapper;
    }

    @Bean(name = "saleMapper")
    public ModelMapper saleMapper() {
        ModelMapper mapper = new ModelMapper();

        //Lectura GET
        mapper.createTypeMap(Sale.class, SaleDTO.class)
                .addMapping(e -> e.getClient().getPrimaryName(), (dest, v) -> dest.getClient().setFirstName((String) v))
                .addMapping(e -> e.getClient().getLastName(), (dest, v) -> dest.getClient().setSurname((String) v));

        return mapper;
    }
}
