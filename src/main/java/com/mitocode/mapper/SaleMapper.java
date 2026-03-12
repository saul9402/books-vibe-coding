package com.mitocode.mapper;

import com.mitocode.dto.SaleDTO;
import com.mitocode.model.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ClientMapper.class, SaleDetailMapper.class})
public interface SaleMapper {

    // Rename Sale fields to SaleDTO convention
    @Mapping(source = "moment", target = "momentSale")
    @Mapping(source = "total",  target = "totalSale")
    @Mapping(source = "status", target = "statusSale")
    SaleDTO toDto(Sale sale);

    @Mapping(source = "momentSale", target = "moment")
    @Mapping(source = "totalSale",  target = "total")
    @Mapping(source = "statusSale", target = "status")
    Sale toEntity(SaleDTO dto);
}

