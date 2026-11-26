
package com.cartera.backend.controller;

import com.cartera.backend.dto.request.CobroRequest;
import com.cartera.backend.dto.request.CobroUpdateRequest;
import com.cartera.backend.dto.request.EstadoCobroRequest;
import com.cartera.backend.dto.response.CobroResponse;
import com.cartera.backend.service.CobroService;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDate;
import java.util.List;

@PreAuthorize("hasAnyRole('ADMINISTRADOR','ANALISTA')")
@RestController
@RequestMapping("/api/cobros")
public class CobroController {

        private final CobroService cobroService;

        // Inyección de dependencias
        public CobroController(CobroService cobroService) {

                this.cobroService = cobroService;
        }

        // Consultar todos los cobros
        @PreAuthorize("hasAnyRole('ADMINISTRADOR','ANALISTA')")
        @GetMapping
        public ResponseEntity<List<CobroResponse>> listarCobros() {

                return ResponseEntity.ok(
                                cobroService.listarCobrosResponse());
        }

        // Consultar cobro por ID
        @PreAuthorize("hasAnyRole('ADMINISTRADOR','ANALISTA') or @cobroSecurity.esCobroPropio(#id, authentication)")
        @GetMapping("/{id}")
        public ResponseEntity<CobroResponse> buscarPorId(
                        @PathVariable("id") Long id) {

                return ResponseEntity.ok(
                                cobroService.buscarPorIdResponse(id));
        }

        // Consultar cobros por cliente
        @PreAuthorize("hasAnyRole('ADMINISTRADOR','ANALISTA')")
        @GetMapping("/cliente/{clienteId}")
        public ResponseEntity<List<CobroResponse>> listarPorCliente(
                        @PathVariable("clienteId") Long clienteId) {

                return ResponseEntity.ok(
                                cobroService.listarPorClienteResponse(clienteId));
        }

        // Consultar cobros por usuario
        @PreAuthorize("hasAnyRole('ADMINISTRADOR','ANALISTA') or @cobroSecurity.esUsuarioPropio(#usuarioId, authentication)")
        @GetMapping("/usuario/{usuarioId}")
        public ResponseEntity<List<CobroResponse>> listarPorUsuario(
                        @PathVariable("usuarioId") Long usuarioId) {

                return ResponseEntity.ok(
                                cobroService.listarPorUsuarioResponse(usuarioId));
        }

        // Consultar cobros por estado
        @PreAuthorize("hasAnyRole('ADMINISTRADOR','ANALISTA')")
        @GetMapping("/estado/{estado}")
        public ResponseEntity<List<CobroResponse>> listarPorEstado(
                        @PathVariable("estado") String estado) {

                return ResponseEntity.ok(
                                cobroService.listarPorEstadoResponse(estado));
        }

        // Consultar cobros por método de pago
        @PreAuthorize("hasAnyRole('ADMINISTRADOR','ANALISTA')")
        @GetMapping("/metodo/{metodoPago}")
        public ResponseEntity<List<CobroResponse>> listarPorMetodoPago(
                        @PathVariable("metodoPago") String metodoPago) {

                return ResponseEntity.ok(
                                cobroService.listarPorMetodoPagoResponse(metodoPago));
        }

        // Consultar cobros por rango de fechas
        @PreAuthorize("hasAnyRole('ADMINISTRADOR','ANALISTA')")
        @GetMapping("/fechas")
        public ResponseEntity<List<CobroResponse>> listarPorFechas(

                        @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,

                        @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

                return ResponseEntity.ok(
                                cobroService.listarPorFechasResponse(
                                                fechaInicio, fechaFin));
        }

        @PostMapping
        @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'REPRESENTANTE')")
        public ResponseEntity<CobroResponse> crearCobro(
                        @Valid @RequestBody CobroRequest request,
                        @AuthenticationPrincipal Jwt jwt) {

                Number usuarioId = jwt.getClaim("usuarioId");

                CobroResponse respuesta = cobroService.crearCobroResponse(
                                request.clienteId(),
                                usuarioId.longValue(),
                                request.valor(),
                                request.fechaPago(),
                                request.metodoPago(),
                                request.referencia(),
                                request.observaciones());

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(respuesta);
        }

        // Actualizar información de un cobro
        @PreAuthorize("hasAnyRole('ADMINISTRADOR','ANALISTA') or @cobroSecurity.esCobroPropio(#id, authentication)")
        @PutMapping("/{id}")
        public ResponseEntity<CobroResponse> actualizarCobro(
                        @PathVariable("id") Long id,
                        @Valid @RequestBody CobroUpdateRequest request) {

                CobroResponse cobro = cobroService.actualizarCobroResponse(
                                id,
                                request.clienteId(),
                                request.valor(),
                                request.fechaPago(),
                                request.metodoPago(),
                                request.referencia(),
                                request.observaciones());

                return ResponseEntity.ok(cobro);
        }

        // Validar o rechazar un cobro
        @PatchMapping("/{id}/estado")
        public ResponseEntity<CobroResponse> cambiarEstado(
                        @PathVariable("id") Long id,
                        @Valid @RequestBody EstadoCobroRequest request) {

                return ResponseEntity.ok(
                                cobroService.cambiarEstadoResponse(
                                                id, request.estado()));
        }
}
