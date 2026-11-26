
package com.cartera.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RolRequest(

        @NotBlank(message = "El nombre del rol es obligatorio")
        @Size(max = 50,
              message = "El nombre no puede superar los 50 caracteres")
        String nombre

) {
}

