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

    @NotNull
    @Min(value = 1)
    @Max(value = 120)
    private Integer age;

    @Size(max = 100)
    private String employment;
}
