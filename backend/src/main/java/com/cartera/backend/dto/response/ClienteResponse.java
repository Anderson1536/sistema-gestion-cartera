
package com.cartera.backend.dto.response;

import java.time.LocalDateTime;

public record ClienteResponse(

        Long id,
        String documento,
        String nombre,
        String correo,
        String telefono,
        String direccion,
        String ciudad,
        Boolean activo,
        LocalDateTime fechaCreacion

) {
}

