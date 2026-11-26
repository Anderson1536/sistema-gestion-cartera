
package com.cartera.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EstadoCobroRequest(

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 20,
              message = "El estado no puede superar los 20 caracteres")
        String estado

) {
}

