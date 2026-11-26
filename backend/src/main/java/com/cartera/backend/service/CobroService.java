
package com.cartera.backend.service;

import com.cartera.backend.entity.Cliente;
import com.cartera.backend.entity.Cobro;
import com.cartera.backend.entity.Usuario;
import com.cartera.backend.repository.CobroRepository;
import com.cartera.backend.dto.response.CobroResponse;
import com.cartera.backend.mapper.CobroMapper;

import com.cartera.backend.exception.BadRequestException;
import com.cartera.backend.exception.ResourceNotFoundException;
import com.cartera.backend.exception.ConflictException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class CobroService {

    private final CobroRepository cobroRepository;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;
    private final CobroMapper cobroMapper;

    private static final Set<String> METODOS_PERMITIDOS =
            Set.of(
                    "EFECTIVO",
                    "TRANSFERENCIA",
                    "CONSIGNACION",
                    "CHEQUE"
            );

    private static final Set<String> ESTADOS_PERMITIDOS =
            Set.of(
                    "PENDIENTE",
                    "VALIDADO",
                    "RECHAZADO"
            );

    private static final BigDecimal VALOR_MAXIMO =
            new BigDecimal("9999999999999.99");

    private static final ZoneId ZONA_HORARIA =
            ZoneId.of("America/Bogota");

    // Inyección de dependencias
    public CobroService(
            CobroRepository cobroRepository,
            ClienteService clienteService,
            UsuarioService usuarioService,
            CobroMapper cobroMapper) {

        this.cobroRepository = cobroRepository;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
        this.cobroMapper = cobroMapper;
    }

    // Listar todos los cobros
    public List<Cobro> listarCobros() {

        return cobroRepository
                .findAllByOrderByFechaRegistroDesc();
    }

    // Buscar cobro por ID
    public Cobro buscarPorId(Long id) {

        return cobroRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cobro no encontrado con ID: " + id
                        ));
    }

    // Listar cobros por cliente
    public List<Cobro> listarPorCliente(Long clienteId) {

        clienteService.buscarPorId(clienteId);

        return cobroRepository.findByClienteId(clienteId);
    }

    // Listar cobros por usuario
    public List<Cobro> listarPorUsuario(Long usuarioId) {

        usuarioService.buscarPorId(usuarioId);

        return cobroRepository.findByUsuarioId(usuarioId);
    }

    // Listar cobros por estado
    public List<Cobro> listarPorEstado(String estado) {

        String estadoNormalizado =
                normalizarEstado(estado);

        return cobroRepository.findByEstado(
                estadoNormalizado);
    }

    // Listar cobros por método de pago
    public List<Cobro> listarPorMetodoPago(String metodoPago) {

        String metodoNormalizado =
                normalizarMetodoPago(metodoPago);

        return cobroRepository.findByMetodoPago(
                metodoNormalizado);
    }

    // Listar cobros por rango de fechas
    public List<Cobro> listarPorFechas(
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        if (fechaInicio == null || fechaFin == null) {

            throw new BadRequestException(
                    "Las fechas de inicio y fin son obligatorias"
            );
        }

        if (fechaInicio.isAfter(fechaFin)) {

            throw new BadRequestException(
                    "La fecha de inicio no puede ser posterior a la fecha de fin"
            );
        }

        return cobroRepository.findByFechaPagoBetween(
                fechaInicio,
                fechaFin
        );
    }

    // Registrar un nuevo cobro
    @Transactional
    public Cobro crearCobro(
            Long clienteId,
            Long usuarioId,
            BigDecimal valor,
            LocalDate fechaPago,
            String metodoPago,
            String referencia,
            String observaciones) {

        Cliente cliente = obtenerClienteActivo(clienteId);

        Usuario usuario = obtenerUsuarioActivo(usuarioId);

        BigDecimal valorValidado = validarValor(valor);

        validarFechaPago(fechaPago);

        String metodoNormalizado =
                normalizarMetodoPago(metodoPago);

        String referenciaValidada =
                validarCampoOpcional(
                        referencia, 100, "referencia");

        String observacionesValidadas =
                validarCampoOpcional(
                        observaciones, 500, "observaciones");

        Cobro cobro = new Cobro(
                cliente,
                usuario,
                valorValidado,
                fechaPago,
                metodoNormalizado,
                referenciaValidada,
                observacionesValidadas
        );

        cobro.setEstado("PENDIENTE");

        return cobroRepository.save(cobro);
    }

    // Actualizar un cobro
    @Transactional
    public Cobro actualizarCobro(
            Long id,
            Long clienteId,
            BigDecimal valor,
            LocalDate fechaPago,
            String metodoPago,
            String referencia,
            String observaciones) {

        Cobro cobro = buscarPorId(id);

        if ("VALIDADO".equals(cobro.getEstado())) {

            throw new ConflictException(
                    "No se puede modificar un cobro que ya fue validado"
            );
        }

        Cliente cliente = obtenerClienteActivo(clienteId);

        BigDecimal valorValidado = validarValor(valor);

        validarFechaPago(fechaPago);

        String metodoNormalizado =
                normalizarMetodoPago(metodoPago);

        String referenciaValidada =
                validarCampoOpcional(
                        referencia, 100, "referencia");

        String observacionesValidadas =
                validarCampoOpcional(
                        observaciones, 500, "observaciones");

        cobro.setCliente(cliente);
        cobro.setValor(valorValidado);
        cobro.setFechaPago(fechaPago);
        cobro.setMetodoPago(metodoNormalizado);
        cobro.setReferencia(referenciaValidada);
        cobro.setObservaciones(observacionesValidadas);

        cobro.setEstado("PENDIENTE");

        return cobroRepository.save(cobro);
    }

    // Validar o rechazar un cobro
    @Transactional
    public Cobro cambiarEstado(
            Long id,
            String nuevoEstado) {

        Cobro cobro = buscarPorId(id);

        String estadoNormalizado =
                normalizarEstado(nuevoEstado);

        if (!"PENDIENTE".equals(cobro.getEstado())) {

            throw new ConflictException(
                    "Solo se puede cambiar el estado de un cobro pendiente"
            );
        }

        if ("PENDIENTE".equals(estadoNormalizado)) {

            throw new BadRequestException(
                    "El nuevo estado debe ser VALIDADO o RECHAZADO"
            );
        }

        cobro.setEstado(estadoNormalizado);

        return cobroRepository.save(cobro);
    }

    // Contar todos los cobros
    public long contarCobros() {

        return cobroRepository.count();
    }

    // Contar cobros por estado
    public long contarPorEstado(String estado) {

        String estadoNormalizado =
                normalizarEstado(estado);

        return cobroRepository.countByEstado(
                estadoNormalizado);
    }

    // Calcular el valor total de cobros validados
    public BigDecimal calcularTotalValidado() {

        return cobroRepository.findByEstado("VALIDADO")
                .stream()
                .map(Cobro::getValor)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    // Obtener cliente activo
    private Cliente obtenerClienteActivo(Long clienteId) {

        if (clienteId == null) {

            throw new BadRequestException(
                    "El cliente es obligatorio"
            );
        }

        Cliente cliente =
                clienteService.buscarPorId(clienteId);

        if (!Boolean.TRUE.equals(cliente.getActivo())) {

            throw new ConflictException(
                    "No se pueden registrar cobros para un cliente inactivo"
            );
        }

        return cliente;
    }

    // Obtener usuario activo
    private Usuario obtenerUsuarioActivo(Long usuarioId) {

        if (usuarioId == null) {

            throw new BadRequestException(
                    "El usuario es obligatorio"
            );
        }

        Usuario usuario =
                usuarioService.buscarPorId(usuarioId);

        if (!Boolean.TRUE.equals(usuario.getActivo())) {

            throw new ConflictException(
                    "No se pueden registrar cobros con un usuario inactivo"
            );
        }

        return usuario;
    }

    // Validar valor del cobro
    private BigDecimal validarValor(BigDecimal valor) {

        if (valor == null) {

            throw new BadRequestException(
                    "El valor del cobro es obligatorio"
            );
        }

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException(
                    "El valor del cobro debe ser mayor que cero"
            );
        }

        if (valor.compareTo(VALOR_MAXIMO) > 0) {

            throw new BadRequestException(
                    "El valor del cobro supera el máximo permitido"
            );
        }

        if (valor.stripTrailingZeros().scale() > 2) {

            throw new BadRequestException(
                    "El valor del cobro no puede tener más de dos decimales"
            );
        }

        return valor.setScale(
                2,
                RoundingMode.UNNECESSARY
        );
    }

    // Validar fecha de pago
    private void validarFechaPago(LocalDate fechaPago) {

        if (fechaPago == null) {

            throw new BadRequestException(
                    "La fecha de pago es obligatoria"
            );
        }

        if (fechaPago.isAfter(
                LocalDate.now(ZONA_HORARIA))) {

            throw new BadRequestException(
                    "La fecha de pago no puede ser futura"
            );
        }
    }

    // Normalizar método de pago
    private String normalizarMetodoPago(String metodoPago) {

        if (metodoPago == null || metodoPago.isBlank()) {

            throw new BadRequestException(
                    "El método de pago es obligatorio"
            );
        }

        String metodoNormalizado =
                metodoPago.trim().toUpperCase(Locale.ROOT);

        if (!METODOS_PERMITIDOS.contains(
                metodoNormalizado)) {

            throw new BadRequestException(
                    "El método de pago no está permitido"
            );
        }

        return metodoNormalizado;
    }

    // Normalizar estado
    private String normalizarEstado(String estado) {

        if (estado == null || estado.isBlank()) {

            throw new BadRequestException(
                    "El estado del cobro es obligatorio"
            );
        }

        String estadoNormalizado =
                estado.trim().toUpperCase(Locale.ROOT);

        if (!ESTADOS_PERMITIDOS.contains(
                estadoNormalizado)) {

            throw new BadRequestException(
                    "El estado del cobro no está permitido"
            );
        }

        return estadoNormalizado;
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

    // Métodos de respuesta mediante DTO

    public List<CobroResponse> listarCobrosResponse() {

        return listarCobros()
                .stream()
                .map(cobroMapper::toResponse)
                .toList();
    }

    public CobroResponse buscarPorIdResponse(Long id) {

        return cobroMapper.toResponse(
                buscarPorId(id));
    }

    public List<CobroResponse> listarPorClienteResponse(
            Long clienteId) {

        return listarPorCliente(clienteId)
                .stream()
                .map(cobroMapper::toResponse)
                .toList();
    }

    public List<CobroResponse> listarPorUsuarioResponse(
            Long usuarioId) {

        return listarPorUsuario(usuarioId)
                .stream()
                .map(cobroMapper::toResponse)
                .toList();
    }

    public List<CobroResponse> listarPorEstadoResponse(
            String estado) {

        return listarPorEstado(estado)
                .stream()
                .map(cobroMapper::toResponse)
                .toList();
    }

    public List<CobroResponse> listarPorMetodoPagoResponse(
            String metodoPago) {

        return listarPorMetodoPago(metodoPago)
                .stream()
                .map(cobroMapper::toResponse)
                .toList();
    }

    public List<CobroResponse> listarPorFechasResponse(
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        return listarPorFechas(fechaInicio, fechaFin)
                .stream()
                .map(cobroMapper::toResponse)
                .toList();
    }

    @Transactional
    public CobroResponse crearCobroResponse(
            Long clienteId,
            Long usuarioId,
            BigDecimal valor,
            LocalDate fechaPago,
            String metodoPago,
            String referencia,
            String observaciones) {

        Cobro cobro = crearCobro(
                clienteId,
                usuarioId,
                valor,
                fechaPago,
                metodoPago,
                referencia,
                observaciones
        );

        return cobroMapper.toResponse(cobro);
    }

    @Transactional
    public CobroResponse actualizarCobroResponse(
            Long id,
            Long clienteId,
            BigDecimal valor,
            LocalDate fechaPago,
            String metodoPago,
            String referencia,
            String observaciones) {

        Cobro cobro = actualizarCobro(
                id,
                clienteId,
                valor,
                fechaPago,
                metodoPago,
                referencia,
                observaciones
        );

        return cobroMapper.toResponse(cobro);
    }

    @Transactional
    public CobroResponse cambiarEstadoResponse(
            Long id,
            String estado) {

        Cobro cobro = cambiarEstado(id, estado);

        return cobroMapper.toResponse(cobro);
    }
}

