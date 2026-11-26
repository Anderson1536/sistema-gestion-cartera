
package com.cartera.backend.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CobroRequest(

        @NotNull(message = "El cliente es obligatorio")
        @Positive(message = "El ID del cliente debe ser positivo")
        Long clienteId,

        @NotNull(message = "El valor del cobro es obligatorio")
        @DecimalMin(value = "0.01",
                    message = "El valor debe ser mayor que cero")
        @Digits(integer = 13, fraction = 2,
                message = "El valor debe tener máximo 13 enteros y 2 decimales")
        BigDecimal valor,

        @NotNull(message = "La fecha de pago es obligatoria")
        LocalDate fechaPago,

        @NotBlank(message = "El método de pago es obligatorio")
        @Size(max = 30,
              message = "El método de pago no puede superar los 30 caracteres")
        String metodoPago,

        @Size(max = 100,
              message = "La referencia no puede superar los 100 caracteres")
        String referencia,

        @Size(max = 500,
              message = "Las observaciones no pueden superar los 500 caracteres")
        String observaciones

) {
}

