package com.mitocode.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDTO {

    private String idAuthor;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String country;
}
