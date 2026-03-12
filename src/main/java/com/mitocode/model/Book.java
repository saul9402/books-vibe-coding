package com.mitocode.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book {

    @Id
    private Integer idBook;

    @ManyToOne
    @JoinColumn(name = "id_category", nullable = false, foreignKey = @ForeignKey(name = "FK_BOOK_CATEGORY"))
    private Category category;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 30)
    private String isbn;

    @Column(nullable = false, length = 100)
    private String photoUrl;

    @Column(nullable = false)
    private boolean status;
}
