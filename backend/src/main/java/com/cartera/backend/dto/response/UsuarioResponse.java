
package com.cartera.backend.dto.response;

import java.time.LocalDateTime;
import com.cartera.backend.dto.response.UsuarioResponse;

public record UsuarioResponse(

        Long id,
        String nombre,
        String correo,
        Boolean activo,
        LocalDateTime fechaCreacion,
        Long rolId,
        String rolNombre

) {
}

