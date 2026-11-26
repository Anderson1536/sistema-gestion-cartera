
package com.cartera.backend.dto.request;

import jakarta.validation.constraints.*;

public record UsuarioRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100,
              message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo electrónico no es válido")
        @Size(max = 150,
              message = "El correo no puede superar los 150 caracteres")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8,
              message = "La contraseña debe tener mínimo 8 caracteres")
        String contrasena,

        @NotNull(message = "El rol es obligatorio")
        @Positive(message = "El identificador del rol debe ser positivo")
        Long rolId

) {
}

