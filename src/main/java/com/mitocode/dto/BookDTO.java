package com.mitocode.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {

    private Integer idBook;

    @NotNull
    @Min(value = 1)
    @Max(value = 100)
    private Integer idCategory;

    @NotNull
    private String title;

    @NotNull
    private String isbn;

    @NotNull
    private String photoUrl;

    @NotNull
    private boolean status;
}
