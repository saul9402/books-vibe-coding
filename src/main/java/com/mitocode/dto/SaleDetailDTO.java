package com.mitocode.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleDetailDTO {

    //@NotNull
    @JsonBackReference
    private SaleDTO sale;

    @NotNull
    private BookDTO book;

    @NotNull
    private double unitPrice;

    @NotNull
    private short quantity;

    @NotNull
    private boolean status;
}
