package com.mitocode.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {

    private String idBook;

    @NotNull
    private String idCategory;

    @NotNull
    private String title;

    @NotNull
    private String isbn;

    @NotNull
    private String photoUrl;

    @NotNull
    private boolean status;
}
