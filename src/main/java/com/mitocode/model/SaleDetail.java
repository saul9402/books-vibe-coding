package com.mitocode.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SaleDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSaleDetail;

    @ManyToOne
    @JoinColumn(name = "id_sale", nullable = false, foreignKey = @ForeignKey(name = "FK_DETAIL_SALE"))
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "id_book", nullable = false, foreignKey = @ForeignKey(name = "FK_DETAIL_BOOK"))
    private Book book;

    @Column(nullable = false, columnDefinition = "decimal(6,2)")
    private double unitPrice;

    @Column(nullable = false)
    private short quantity;

    @Column(nullable = false)
    private boolean status;


}
