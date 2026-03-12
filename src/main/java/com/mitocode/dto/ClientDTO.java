package com.mitocode.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {

    private String idClient;

    @NotNull
    //@NotEmpty
    //@NotBlank
    @Size(min = 3, max = 20)
    private String firstName;

    @NotNull
    @Size(min = 3, max = 20)
    private String surname;

    @NotNull
    private LocalDate birthDateClient;

    /*@Email
    @Pattern(regexp = "[0-9]+")
    @Max(value = 99)
    @Min(value = 1)*/
}
