
package com.cartera.backend.service;

import com.cartera.backend.entity.Rol;
import com.cartera.backend.repository.RolRepository;

import com.cartera.backend.exception.BadRequestException;
import com.cartera.backend.exception.ResourceNotFoundException;
import com.cartera.backend.exception.ConflictException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class RolService {

    private final RolRepository rolRepository;

    private static final Set<String> ROLES_PERMITIDOS =
            Set.of(
                    "ADMINISTRADOR",
                    "ANALISTA",
                    "REPRESENTANTE"
            );

    // Inyección de dependencias
    public RolService(RolRepository rolRepository) {

        this.rolRepository = rolRepository;
    }

    // Listar todos los roles
    public List<Rol> listarRoles() {

        return rolRepository.findAll();
    }

    // Buscar un rol por ID
    public Rol buscarPorId(Long id) {

        return rolRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rol no encontrado con ID: " + id
                        ));
    }

    // Buscar un rol por nombre
    public Rol buscarPorNombre(String nombre) {

        String nombreNormalizado =
                normalizarNombre(nombre);

        return rolRepository.findByNombre(nombreNormalizado)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rol no encontrado con nombre: "
                                        + nombreNormalizado
                        ));
    }

    // Registrar un nuevo rol
    @Transactional
    public Rol crearRol(String nombre) {

        String nombreNormalizado =
                normalizarNombre(nombre);

        if (!ROLES_PERMITIDOS.contains(nombreNormalizado)) {

            throw new BadRequestException(
                    "El nombre del rol no está permitido"
            );
        }

        if (rolRepository.existsByNombre(nombreNormalizado)) {

            throw new ConflictException(
                    "Ya existe un rol con el nombre: "
                            + nombreNormalizado
            );
        }

        Rol rol = new Rol(nombreNormalizado);

        return rolRepository.save(rol);
    }

    // Normalizar y validar el nombre del rol
    private String normalizarNombre(String nombre) {

        if (nombre == null || nombre.isBlank()) {

            throw new BadRequestException(
                    "El nombre del rol es obligatorio"
            );
        }

        return nombre.trim().toUpperCase(Locale.ROOT);
    }
}

