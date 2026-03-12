package com.mitocode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("sales")
public class Sale {

    @Id
    private String idSale;

    private Client client;              // snapshot del cliente en el momento de la venta

    private LocalDateTime moment;

    private double total;

    private boolean status;

    private List<SaleDetail> details;  // subdocumentos embebidos
}
