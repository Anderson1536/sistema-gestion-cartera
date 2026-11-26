
package com.cartera.backend.dto.response;

import java.time.OffsetDateTime;
import java.util.Map;

public record ErrorResponse(

        OffsetDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String path,
        Map<String, String> detalles

) {
}

