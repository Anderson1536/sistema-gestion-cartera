
package com.cartera.backend.service;

import com.cartera.backend.entity.Rol;
import com.cartera.backend.entity.Usuario;
import com.cartera.backend.repository.UsuarioRepository;
import com.cartera.backend.dto.response.UsuarioResponse;
import com.cartera.backend.mapper.UsuarioMapper;

import com.cartera.backend.exception.BadRequestException;
import com.cartera.backend.exception.ResourceNotFoundException;
import com.cartera.backend.exception.ConflictException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    // Inyección de dependencias
    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolService rolService,
            PasswordEncoder passwordEncoder,
            UsuarioMapper usuarioMapper) {

        this.usuarioRepository = usuarioRepository;
        this.rolService = rolService;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

    // Listar todos los usuarios
    public List<Usuario> listarUsuarios() {

        return usuarioRepository.findAll();
    }

    // Listar usuarios activos
    public List<Usuario> listarUsuariosActivos() {

        return usuarioRepository.findByActivoTrue();
    }

    // Buscar usuario por ID
    public Usuario buscarPorId(Long id) {

        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario no encontrado con ID: " + id
                        ));
    }

    // Buscar usuario por correo
    public Usuario buscarPorCorreo(String correo) {

        String correoNormalizado =
                normalizarCorreo(correo);

        return usuarioRepository.findByCorreo(correoNormalizado)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario no encontrado"
                        ));
    }

    // Listar usuarios por rol
    public List<Usuario> listarPorRol(String nombreRol) {

        Rol rol = rolService.buscarPorNombre(nombreRol);

        return usuarioRepository.findByRolNombre(
                rol.getNombre());
    }

    // Registrar un nuevo usuario
    @Transactional
    public Usuario crearUsuario(
            String nombre,
            String correo,
            String contrasena,
            Long rolId) {

        validarNombre(nombre);
        validarContrasena(contrasena);

        String correoNormalizado =
                normalizarCorreo(correo);

        if (usuarioRepository.existsByCorreo(correoNormalizado)) {

            throw new ConflictException(
                    "Ya existe un usuario con el correo indicado"
            );
        }

        if (rolId == null) {

            throw new BadRequestException(
                    "El rol es obligatorio"
            );
        }

        Rol rol = rolService.buscarPorId(rolId);

        String contrasenaCodificada =
                passwordEncoder.encode(contrasena);

        Usuario usuario = new Usuario(
                nombre.trim(),
                correoNormalizado,
                contrasenaCodificada,
                rol
        );

        return usuarioRepository.save(usuario);
    }

    // Actualizar información del usuario
    @Transactional
    public Usuario actualizarUsuario(
            Long id,
            String nombre,
            String correo,
            Long rolId) {

        Usuario usuario = buscarPorId(id);

        validarNombre(nombre);

        String correoNormalizado =
                normalizarCorreo(correo);

        if (!usuario.getCorreo().equals(correoNormalizado)
                && usuarioRepository.existsByCorreo(
                        correoNormalizado)) {

            throw new ConflictException(
                    "Ya existe otro usuario con el correo indicado"
            );
        }

        if (rolId == null) {

            throw new BadRequestException(
                    "El rol es obligatorio"
            );
        }

        Rol rol = rolService.buscarPorId(rolId);

        usuario.setNombre(nombre.trim());
        usuario.setCorreo(correoNormalizado);
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    // Cambiar contraseña
    @Transactional
    public void cambiarContrasena(
            Long id,
            String nuevaContrasena) {

        Usuario usuario = buscarPorId(id);

        validarContrasena(nuevaContrasena);

        usuario.setContrasena(
                passwordEncoder.encode(nuevaContrasena)
        );

        usuarioRepository.save(usuario);
    }

    // Activar o desactivar usuario
    @Transactional
    public Usuario cambiarEstado(Long id, boolean activo) {

        Usuario usuario = buscarPorId(id);

        usuario.setActivo(activo);

        return usuarioRepository.save(usuario);
    }

    // Validar nombre
    private void validarNombre(String nombre) {

        if (nombre == null || nombre.isBlank()) {

            throw new BadRequestException(
                    "El nombre del usuario es obligatorio"
            );
        }

        if (nombre.trim().length() > 100) {

            throw new BadRequestException(
                    "El nombre no puede superar los 100 caracteres"
            );
        }
    }

    // Normalizar y validar correo electrónico
    private String normalizarCorreo(String correo) {

        if (correo == null || correo.isBlank()) {

            throw new BadRequestException(
                    "El correo electrónico es obligatorio"
            );
        }

        String correoNormalizado =
                correo.trim().toLowerCase(Locale.ROOT);

        if (correoNormalizado.length() > 150) {

            throw new BadRequestException(
                    "El correo no puede superar los 150 caracteres"
            );
        }

        if (!correoNormalizado.matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            throw new BadRequestException(
                    "El formato del correo electrónico no es válido"
            );
        }

        return correoNormalizado;
    }

    // Validar contraseña
    private void validarContrasena(String contrasena) {

        if (contrasena == null || contrasena.isBlank()) {

            throw new BadRequestException(
                    "La contraseña es obligatoria"
            );
        }

        if (contrasena.length() < 8) {

            throw new BadRequestException(
                    "La contraseña debe tener mínimo 8 caracteres"
            );
        }

        if (contrasena.getBytes(
                StandardCharsets.UTF_8).length > 72) {

            throw new BadRequestException(
                    "La contraseña no puede superar los 72 bytes"
            );
        }
    }

    // Métodos de respuesta mediante DTO

    public List<UsuarioResponse> listarUsuariosResponse() {

        return listarUsuarios()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    public List<UsuarioResponse> listarUsuariosActivosResponse() {

        return listarUsuariosActivos()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    public UsuarioResponse buscarPorIdResponse(Long id) {

        return usuarioMapper.toResponse(
                buscarPorId(id));
    }

    public UsuarioResponse buscarPorCorreoResponse(
            String correo) {

        return usuarioMapper.toResponse(
                buscarPorCorreo(correo));
    }

    public List<UsuarioResponse> listarPorRolResponse(
            String nombreRol) {

        return listarPorRol(nombreRol)
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Transactional
    public UsuarioResponse crearUsuarioResponse(
            String nombre,
            String correo,
            String contrasena,
            Long rolId) {

        Usuario usuario = crearUsuario(
                nombre,
                correo,
                contrasena,
                rolId
        );

        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse actualizarUsuarioResponse(
            Long id,
            String nombre,
            String correo,
            Long rolId) {

        Usuario usuario = actualizarUsuario(
                id,
                nombre,
                correo,
                rolId
        );

        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse cambiarEstadoResponse(
            Long id,
            boolean activo) {

        Usuario usuario = cambiarEstado(id, activo);

        return usuarioMapper.toResponse(usuario);
    }
}

