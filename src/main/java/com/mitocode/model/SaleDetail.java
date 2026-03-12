package com.mitocode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Sin @Document — es un subdocumento embebido dentro de Sale
public class SaleDetail {

    private Book book;       // snapshot del libro en el momento de la venta

    private double unitPrice;

    private short quantity;

    private boolean status;
}
