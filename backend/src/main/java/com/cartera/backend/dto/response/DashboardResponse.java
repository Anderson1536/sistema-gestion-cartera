
package com.cartera.backend.dto.response;

import java.math.BigDecimal;

public record DashboardResponse(

        long totalCobros,
        long cobrosPendientes,
        long cobrosValidados,
        long cobrosRechazados,
        BigDecimal valorTotalValidado

) {
}

