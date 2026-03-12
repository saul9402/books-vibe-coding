package com.mitocode.mapper;

import com.mitocode.dto.SaleDetailDTO;
import com.mitocode.model.SaleDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface SaleDetailMapper {

    // 'sale' is a bidirectional Jackson ref in DTO only; ignore it during mapping
    @Mapping(target = "sale", ignore = true)
    SaleDetailDTO toDto(SaleDetail detail);

    SaleDetail toEntity(SaleDetailDTO dto);
}

