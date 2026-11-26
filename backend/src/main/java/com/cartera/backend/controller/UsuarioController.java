
package com.cartera.backend.controller;

import com.cartera.backend.dto.request.UsuarioRequest;
import com.cartera.backend.dto.request.UsuarioUpdateRequest;
import com.cartera.backend.dto.request.CambioContrasenaRequest;
import com.cartera.backend.dto.request.EstadoRequest;
import com.cartera.backend.dto.response.UsuarioResponse;
import com.cartera.backend.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

        private final UsuarioService usuarioService;

        // Inyección de dependencias
        public UsuarioController(UsuarioService usuarioService) {

                this.usuarioService = usuarioService;
        }

        // Consultar todos los usuarios
        @GetMapping
        public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {

                return ResponseEntity.ok(
                                usuarioService.listarUsuariosResponse());
        }

        // Consultar usuarios activos
        @GetMapping("/activos")
        public ResponseEntity<List<UsuarioResponse>> listarActivos() {

                return ResponseEntity.ok(
                                usuarioService.listarUsuariosActivosResponse());
        }

        // Consultar usuario por ID
        @GetMapping("/{id}")
        public ResponseEntity<UsuarioResponse> buscarPorId(
                        @PathVariable("id") Long id) {

                return ResponseEntity.ok(
                                usuarioService.buscarPorIdResponse(id));
        }

        // Consultar usuario por correo
        @GetMapping("/correo")
        public ResponseEntity<UsuarioResponse> buscarPorCorreo(
                        @RequestParam("correo") String correo) {

                return ResponseEntity.ok(
                                usuarioService.buscarPorCorreoResponse(correo));
        }

        // Consultar usuarios por rol
        @GetMapping("/rol/{nombre}")
        public ResponseEntity<List<UsuarioResponse>> listarPorRol(
                        @PathVariable("nombre") String nombre) {

                return ResponseEntity.ok(
                                usuarioService.listarPorRolResponse(nombre));
        }

        // Registrar un nuevo usuario
        @PostMapping
        public ResponseEntity<UsuarioResponse> crearUsuario(
                        @Valid @RequestBody UsuarioRequest request) {

                UsuarioResponse usuario = usuarioService.crearUsuarioResponse(
                                request.nombre(),
                                request.correo(),
                                request.contrasena(),
                                request.rolId());

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(usuario);
        }

        // Actualizar información de un usuario
        @PutMapping("/{id}")
        @PreAuthorize("#id != authentication.token.claims['usuarioId']")
        public ResponseEntity<UsuarioResponse> actualizarUsuario(
                        @PathVariable("id") Long id,
                        @Valid @RequestBody UsuarioUpdateRequest request) {

                UsuarioResponse usuario = usuarioService.actualizarUsuarioResponse(
                                id,
                                request.nombre(),
                                request.correo(),
                                request.rolId());

                return ResponseEntity.ok(usuario);
        }

        // Restablecer contraseña
        @PatchMapping("/{id}/contrasena")
        public ResponseEntity<Void> cambiarContrasena(
                        @PathVariable("id") Long id,
                        @Valid @RequestBody CambioContrasenaRequest request) {

                usuarioService.cambiarContrasena(
                                id, request.nuevaContrasena());

                return ResponseEntity.noContent().build();
        }

        // Activar o desactivar usuario
        @PatchMapping("/{id}/estado")
        @PreAuthorize("#id != authentication.token.claims['usuarioId']")
        public ResponseEntity<UsuarioResponse> cambiarEstado(
                        @PathVariable("id") Long id,
                        @Valid @RequestBody EstadoRequest request) {

                return ResponseEntity.ok(
                                usuarioService.cambiarEstadoResponse(
                                                id, request.activo()));
        }
}
