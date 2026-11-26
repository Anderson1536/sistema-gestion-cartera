
package com.cartera.backend.controller;

import com.cartera.backend.dto.request.RolRequest;
import com.cartera.backend.dto.response.RolResponse;
import com.cartera.backend.entity.Rol;
import com.cartera.backend.mapper.RolMapper;
import com.cartera.backend.service.RolService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;
    private final RolMapper rolMapper;

    // Inyección de dependencias
    public RolController(
            RolService rolService,
            RolMapper rolMapper) {

        this.rolService = rolService;
        this.rolMapper = rolMapper;
    }

    // Consultar todos los roles
    @GetMapping
    public ResponseEntity<List<RolResponse>> listarRoles() {

        List<RolResponse> roles = rolService.listarRoles()
                .stream()
                .map(rolMapper::toResponse)
                .toList();

        return ResponseEntity.ok(roles);
    }

    // Consultar un rol por ID
    @GetMapping("/{id}")
    public ResponseEntity<RolResponse> buscarPorId(
            @PathVariable("id") Long id) {

        Rol rol = rolService.buscarPorId(id);

        return ResponseEntity.ok(
                rolMapper.toResponse(rol));
    }

    // Consultar un rol por nombre
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<RolResponse> buscarPorNombre(
            @PathVariable("nombre") String nombre) {

        Rol rol = rolService.buscarPorNombre(nombre);

        return ResponseEntity.ok(
                rolMapper.toResponse(rol));
    }

    // Registrar un nuevo rol
    @PostMapping
    public ResponseEntity<RolResponse> crearRol(
            @Valid @RequestBody RolRequest request) {

        Rol rol = rolService.crearRol(request.nombre());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(rolMapper.toResponse(rol));
    }
}
