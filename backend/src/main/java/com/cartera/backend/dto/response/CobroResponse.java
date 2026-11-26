
package com.cartera.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CobroResponse(

        Long id,

        Long clienteId,
        String clienteDocumento,
        String clienteNombre,

        Long usuarioId,
        String usuarioNombre,

        BigDecimal valor,
        LocalDate fechaPago,
        LocalDateTime fechaRegistro,
        String metodoPago,
        String referencia,
        String estado,
        String observaciones

) {
}

