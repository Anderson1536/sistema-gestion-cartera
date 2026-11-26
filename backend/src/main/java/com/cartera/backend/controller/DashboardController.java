
package com.cartera.backend.controller;

import com.cartera.backend.dto.response.DashboardResponse;
import com.cartera.backend.service.DashboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    // Inyección de dependencias
    public DashboardController(
            DashboardService dashboardService) {

        this.dashboardService = dashboardService;
    }

    // Consultar indicadores generales
    @GetMapping
    public ResponseEntity<DashboardResponse> obtenerIndicadores() {

        return ResponseEntity.ok(
                dashboardService.obtenerIndicadores());
    }
}

