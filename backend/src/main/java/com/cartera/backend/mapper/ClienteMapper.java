
package com.cartera.backend.mapper;

import com.cartera.backend.dto.response.ClienteResponse;
import com.cartera.backend.entity.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    // Convertir una entidad Cliente en un DTO de respuesta
    public ClienteResponse toResponse(Cliente cliente) {

        return new ClienteResponse(
                cliente.getId(),
                cliente.getDocumento(),
                cliente.getNombre(),
                cliente.getCorreo(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getCiudad(),
                cliente.getActivo(),
                cliente.getFechaCreacion()
        );
    }
}

