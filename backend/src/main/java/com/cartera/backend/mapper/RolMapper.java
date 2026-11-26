
package com.cartera.backend.mapper;

import com.cartera.backend.dto.response.RolResponse;
import com.cartera.backend.entity.Rol;
import org.springframework.stereotype.Component;

@Component
public class RolMapper {

    // Convertir una entidad Rol en un DTO de respuesta
    public RolResponse toResponse(Rol rol) {

        return new RolResponse(
                rol.getId(),
                rol.getNombre()
        );
    }
}

