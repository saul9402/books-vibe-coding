package com.mitocode.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleDTO {

    private String idSale;

    @NotNull
    private ClientDTO client;

    @NotNull
    private LocalDateTime momentSale;

    @NotNull
    private double totalSale;

    @NotNull
    private boolean statusSale;

    @JsonManagedReference
    @NotNull
    private List<SaleDetailDTO> details;
}
