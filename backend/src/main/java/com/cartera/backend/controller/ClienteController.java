
package com.cartera.backend.controller;

import com.cartera.backend.dto.request.ClienteRequest;
import com.cartera.backend.dto.request.EstadoRequest;
import com.cartera.backend.dto.response.ClienteResponse;
import com.cartera.backend.entity.Cliente;
import com.cartera.backend.mapper.ClienteMapper;
import com.cartera.backend.service.ClienteService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final ClienteMapper clienteMapper;

    // Inyección de dependencias
    public ClienteController(
            ClienteService clienteService,
            ClienteMapper clienteMapper) {

        this.clienteService = clienteService;
        this.clienteMapper = clienteMapper;
    }

    // Consultar todos los clientes
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listarClientes() {

        List<ClienteResponse> clientes =
                clienteService.listarClientes()
                        .stream()
                        .map(clienteMapper::toResponse)
                        .toList();

        return ResponseEntity.ok(clientes);
    }

    // Consultar clientes activos
    @GetMapping("/activos")
    public ResponseEntity<List<ClienteResponse>> listarActivos() {

        List<ClienteResponse> clientes =
                clienteService.listarClientesActivos()
                        .stream()
                        .map(clienteMapper::toResponse)
                        .toList();

        return ResponseEntity.ok(clientes);
    }

    // Consultar cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(
            @PathVariable("id") Long id) {

        Cliente cliente = clienteService.buscarPorId(id);

        return ResponseEntity.ok(
                clienteMapper.toResponse(cliente));
    }

    // Consultar cliente por documento
    @GetMapping("/documento/{documento}")
    public ResponseEntity<ClienteResponse> buscarPorDocumento(
            @PathVariable("documento") String documento) {

        Cliente cliente =
                clienteService.buscarPorDocumento(documento);

        return ResponseEntity.ok(
                clienteMapper.toResponse(cliente));
    }

    // Buscar clientes por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteResponse>> buscarPorNombre(
            @RequestParam("nombre") String nombre) {

        List<ClienteResponse> clientes =
                clienteService.buscarPorNombre(nombre)
                        .stream()
                        .map(clienteMapper::toResponse)
                        .toList();

        return ResponseEntity.ok(clientes);
    }

    // Registrar un nuevo cliente
    @PostMapping
    public ResponseEntity<ClienteResponse> crearCliente(
            @Valid @RequestBody ClienteRequest request) {

        Cliente cliente = clienteService.crearCliente(
                request.documento(),
                request.nombre(),
                request.correo(),
                request.telefono(),
                request.direccion(),
                request.ciudad()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteMapper.toResponse(cliente));
    }

    // Actualizar información del cliente
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizarCliente(
            @PathVariable("id") Long id,
            @Valid @RequestBody ClienteRequest request) {

        Cliente cliente = clienteService.actualizarCliente(
                id,
                request.documento(),
                request.nombre(),
                request.correo(),
                request.telefono(),
                request.direccion(),
                request.ciudad()
        );

        return ResponseEntity.ok(
                clienteMapper.toResponse(cliente));
    }

    // Activar o desactivar un cliente
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ClienteResponse> cambiarEstado(
            @PathVariable("id") Long id,
            @Valid @RequestBody EstadoRequest request) {

        Cliente cliente = clienteService.cambiarEstado(
                id, request.activo());

        return ResponseEntity.ok(
                clienteMapper.toResponse(cliente));
    }
}

