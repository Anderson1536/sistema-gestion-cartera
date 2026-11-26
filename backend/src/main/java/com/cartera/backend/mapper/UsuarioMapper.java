
package com.cartera.backend.mapper;

import com.cartera.backend.dto.response.UsuarioResponse;
import com.cartera.backend.entity.Usuario;
import org.springframework.stereotype.Component;
import com.cartera.backend.mapper.UsuarioMapper;

@Component
public class UsuarioMapper {

    // Convertir una entidad Usuario en un DTO de respuesta
    public UsuarioResponse toResponse(Usuario usuario) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getActivo(),
                usuario.getFechaCreacion(),
                usuario.getRol().getId(),
                usuario.getRol().getNombre()
        );
    }
}

