
package com.cartera.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambioContrasenaRequest(

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8,
              message = "La contraseña debe tener mínimo 8 caracteres")
        String nuevaContrasena

) {
}

