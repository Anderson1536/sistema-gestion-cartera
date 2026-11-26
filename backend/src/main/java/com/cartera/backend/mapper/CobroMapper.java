
package com.cartera.backend.mapper;

import com.cartera.backend.dto.response.CobroResponse;
import com.cartera.backend.entity.Cobro;
import org.springframework.stereotype.Component;

@Component
public class CobroMapper {

    // Convertir una entidad Cobro en un DTO de respuesta
    public CobroResponse toResponse(Cobro cobro) {

        return new CobroResponse(

                cobro.getId(),

                cobro.getCliente().getId(),
                cobro.getCliente().getDocumento(),
                cobro.getCliente().getNombre(),

                cobro.getUsuario().getId(),
                cobro.getUsuario().getNombre(),

                cobro.getValor(),
                cobro.getFechaPago(),
                cobro.getFechaRegistro(),
                cobro.getMetodoPago(),
                cobro.getReferencia(),
                cobro.getEstado(),
                cobro.getObservaciones()
        );
    }
}

