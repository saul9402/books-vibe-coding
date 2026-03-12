package com.mitocode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("books")
public class Book {

    @Id
    private String idBook;

    private Category category;  // embebido — Category es pequeña y siempre se lee con Book

    @Indexed
    private String title;

    private String isbn;

    private String photoUrl;

    private boolean status;
}
