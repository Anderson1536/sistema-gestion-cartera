
package com.cartera.backend.dto.request;

import jakarta.validation.constraints.*;

public record ClienteRequest(

        @NotBlank(message = "El documento es obligatorio")
        @Size(max = 20,
              message = "El documento no puede superar los 20 caracteres")
        String documento,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150,
              message = "El nombre no puede superar los 150 caracteres")
        String nombre,

        @Email(message = "El correo electrónico no es válido")
        @Size(max = 150,
              message = "El correo no puede superar los 150 caracteres")
        String correo,

        @Size(max = 20,
              message = "El teléfono no puede superar los 20 caracteres")
        String telefono,

        @Size(max = 200,
              message = "La dirección no puede superar los 200 caracteres")
        String direccion,

        @Size(max = 100,
              message = "La ciudad no puede superar los 100 caracteres")
        String ciudad

) {
}

