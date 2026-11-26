
package com.cartera.backend.service;

import com.cartera.backend.dto.response.DashboardResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final CobroService cobroService;

    // Inyección de dependencias
    public DashboardService(CobroService cobroService) {

        this.cobroService = cobroService;
    }

    // Obtener los indicadores generales del Dashboard
    public DashboardResponse obtenerIndicadores() {

        long totalCobros =
                cobroService.contarCobros();

        long cobrosPendientes =
                cobroService.contarPorEstado("PENDIENTE");

        long cobrosValidados =
                cobroService.contarPorEstado("VALIDADO");

        long cobrosRechazados =
                cobroService.contarPorEstado("RECHAZADO");

        BigDecimal valorTotalValidado =
                cobroService.calcularTotalValidado();

        return new DashboardResponse(
                totalCobros,
                cobrosPendientes,
                cobrosValidados,
                cobrosRechazados,
                valorTotalValidado
        );
    }
}

