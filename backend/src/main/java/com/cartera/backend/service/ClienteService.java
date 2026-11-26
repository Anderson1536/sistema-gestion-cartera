
package com.cartera.backend.service;

import com.cartera.backend.entity.Cliente;
import com.cartera.backend.repository.ClienteRepository;

import com.cartera.backend.exception.BadRequestException;
import com.cartera.backend.exception.ResourceNotFoundException;
import com.cartera.backend.exception.ConflictException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;

    // Inyección de dependencias
    public ClienteService(ClienteRepository clienteRepository) {

        this.clienteRepository = clienteRepository;
    }

    // Listar todos los clientes
    public List<Cliente> listarClientes() {

        return clienteRepository.findAll();
    }

    // Listar clientes activos
    public List<Cliente> listarClientesActivos() {

        return clienteRepository.findByActivoTrue();
    }

    // Buscar cliente por ID
    public Cliente buscarPorId(Long id) {

        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cliente no encontrado con ID: " + id
                        ));
    }

    // Buscar cliente por documento
    public Cliente buscarPorDocumento(String documento) {

        String documentoNormalizado =
                normalizarDocumento(documento);

        return clienteRepository.findByDocumento(
                documentoNormalizado)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cliente no encontrado con documento: "
                                        + documentoNormalizado
                        ));
    }

    // Buscar clientes por nombre
    public List<Cliente> buscarPorNombre(String nombre) {

        if (nombre == null || nombre.isBlank()) {

            throw new BadRequestException(
                    "El nombre de búsqueda es obligatorio"
            );
        }

        return clienteRepository
                .findByNombreContainingIgnoreCase(
                        nombre.trim());
    }

    // Registrar un nuevo cliente
    @Transactional
    public Cliente crearCliente(
            String documento,
            String nombre,
            String correo,
            String telefono,
            String direccion,
            String ciudad) {

        String documentoNormalizado =
                normalizarDocumento(documento);

        if (clienteRepository.existsByDocumento(
                documentoNormalizado)) {

            throw new ConflictException(
                    "Ya existe un cliente con el documento: "
                            + documentoNormalizado
            );
        }

        String nombreValidado = validarNombre(nombre);

        String correoNormalizado =
                normalizarCorreo(correo);

        String telefonoValidado =
                validarCampoOpcional(
                        telefono, 20, "teléfono");

        String direccionValidada =
                validarCampoOpcional(
                        direccion, 200, "dirección");

        String ciudadValidada =
                validarCampoOpcional(
                        ciudad, 100, "ciudad");

        Cliente cliente = new Cliente(
                documentoNormalizado,
                nombreValidado,
                correoNormalizado,
                telefonoValidado,
                direccionValidada,
                ciudadValidada
        );

        return clienteRepository.save(cliente);
    }

    // Actualizar información del cliente
    @Transactional
    public Cliente actualizarCliente(
            Long id,
            String documento,
            String nombre,
            String correo,
            String telefono,
            String direccion,
            String ciudad) {

        Cliente cliente = buscarPorId(id);

        String documentoNormalizado =
                normalizarDocumento(documento);

        if (!cliente.getDocumento().equals(
                documentoNormalizado)
                && clienteRepository.existsByDocumento(
                        documentoNormalizado)) {

            throw new ConflictException(
                    "Ya existe otro cliente con el documento: "
                            + documentoNormalizado
            );
        }

        cliente.setDocumento(documentoNormalizado);

        cliente.setNombre(validarNombre(nombre));

        cliente.setCorreo(normalizarCorreo(correo));

        cliente.setTelefono(
                validarCampoOpcional(
                        telefono, 20, "teléfono"));

        cliente.setDireccion(
                validarCampoOpcional(
                        direccion, 200, "dirección"));

        cliente.setCiudad(
                validarCampoOpcional(
                        ciudad, 100, "ciudad"));

        return clienteRepository.save(cliente);
    }

    // Activar o desactivar cliente
    @Transactional
    public Cliente cambiarEstado(Long id, boolean activo) {

        Cliente cliente = buscarPorId(id);

        cliente.setActivo(activo);

        return clienteRepository.save(cliente);
    }

    // Normalizar y validar documento
    private String normalizarDocumento(String documento) {

        if (documento == null || documento.isBlank()) {

            throw new BadRequestException(
                    "El documento es obligatorio"
            );
        }

        String documentoNormalizado = documento
                .replaceAll("[\\s.\\-]", "")
                .toUpperCase(Locale.ROOT);

        if (documentoNormalizado.isBlank()) {

            throw new BadRequestException(
                    "El documento no puede estar vacío"
            );
        }

        if (documentoNormalizado.length() > 20) {

            throw new BadRequestException(
                    "El documento no puede superar los 20 caracteres"
            );
        }

        if (!documentoNormalizado.matches("[A-Z0-9]+")) {

            throw new BadRequestException(
                    "El documento solo puede contener letras y números"
            );
        }

        return documentoNormalizado;
    }

    // Validar nombre
    private String validarNombre(String nombre) {

        if (nombre == null || nombre.isBlank()) {

            throw new BadRequestException(
                    "El nombre del cliente es obligatorio"
            );
        }

        String nombreValidado = nombre.trim();

        if (nombreValidado.length() > 150) {

            throw new BadRequestException(
                    "El nombre no puede superar los 150 caracteres"
            );
        }

        return nombreValidado;
    }

    // Normalizar y validar correo electrónico
    private String normalizarCorreo(String correo) {

        if (correo == null || correo.isBlank()) {

            return null;
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

    // Validar campos opcionales
    private String validarCampoOpcional(
            String valor,
            int longitudMaxima,
            String campo) {

        if (valor == null || valor.isBlank()) {

            return null;
        }

        String valorValidado = valor.trim();

        if (valorValidado.length() > longitudMaxima) {

            throw new BadRequestException(
                    "El campo " + campo
                            + " no puede superar los "
                            + longitudMaxima + " caracteres"
            );
        }

        return valorValidado;
    }
}


