
package com.cartera.backend.exception;

import com.cartera.backend.dto.response.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        private static final ZoneId ZONA_HORARIA = ZoneId.of("America/Bogota");

        // Error 400: Solicitud incorrecta
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ErrorResponse> manejarBadRequest(
                        BadRequestException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.BAD_REQUEST,
                                ex.getMessage(),
                                request,
                                Map.of());
        }

        // Error 404: Recurso no encontrado
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> manejarRecursoNoEncontrado(
                        ResourceNotFoundException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.NOT_FOUND,
                                ex.getMessage(),
                                request,
                                Map.of());
        }

        // Error 409: Conflicto
        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ErrorResponse> manejarConflicto(
                        ConflictException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.CONFLICT,
                                ex.getMessage(),
                                request,
                                Map.of());
        }

        // Error 400: Validaciones de los DTO
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> manejarValidaciones(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                Map<String, String> detalles = new LinkedHashMap<>();

                ex.getBindingResult().getFieldErrors().forEach(error -> {

                        String campo = error.getField();

                        String mensaje = error.getDefaultMessage() != null
                                        ? error.getDefaultMessage()
                                        : "Valor inválido";

                        detalles.merge(
                                        campo,
                                        mensaje,
                                        (anterior, nuevo) -> anterior + "; " + nuevo);
                });

                return construirRespuesta(
                                HttpStatus.BAD_REQUEST,
                                "La solicitud contiene errores de validación",
                                request,
                                detalles);
        }

        // Error 400: JSON incorrecto o datos incompatibles
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> manejarJsonIncorrecto(
                        HttpMessageNotReadableException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.BAD_REQUEST,
                                "El cuerpo de la solicitud contiene datos inválidos o un formato JSON incorrecto",
                                request,
                                Map.of());
        }

        // Error 400: Parámetro obligatorio ausente
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ErrorResponse> manejarParametroFaltante(
                        MissingServletRequestParameterException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.BAD_REQUEST,
                                "Falta un parámetro obligatorio en la solicitud",
                                request,
                                Map.of(
                                                ex.getParameterName(),
                                                "Este parámetro es obligatorio"));
        }

        // Error 400: Tipo de parámetro incorrecto
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse> manejarTipoIncorrecto(
                        MethodArgumentTypeMismatchException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.BAD_REQUEST,
                                "La solicitud contiene un parámetro con formato incorrecto",
                                request,
                                Map.of(
                                                ex.getName(),
                                                "El valor proporcionado no es válido"));
        }

        // Error 409: Restricciones de integridad de datos
        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponse> manejarIntegridadDatos(
                        DataIntegrityViolationException ex,
                        HttpServletRequest request) {

                logger.warn(
                                "Conflicto de integridad de datos en {}",
                                request.getRequestURI());

                return construirRespuesta(
                                HttpStatus.CONFLICT,
                                "La operación genera un conflicto con las restricciones de la base de datos",
                                request,
                                Map.of());
        }

        // Error 404: Ruta inexistente
        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ErrorResponse> manejarRutaNoEncontrada(
                        NoResourceFoundException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.NOT_FOUND,
                                "La ruta solicitada no existe",
                                request,
                                Map.of());
        }

        // Error 405: Método HTTP no permitido
        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ErrorResponse> manejarMetodoNoPermitido(
                        HttpRequestMethodNotSupportedException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.METHOD_NOT_ALLOWED,
                                "El método HTTP utilizado no está permitido para esta ruta",
                                request,
                                Map.of());
        }

        // Error 415: Tipo de contenido no soportado
        @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
        public ResponseEntity<ErrorResponse> manejarContenidoNoSoportado(
                        HttpMediaTypeNotSupportedException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                                "El tipo de contenido de la solicitud no está soportado",
                                request,
                                Map.of());
        }

        // Error 500: Excepciones inesperadas
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> manejarErrorGeneral(
                        Exception ex,
                        HttpServletRequest request) {

                logger.error(
                                "Error interno en la ruta {}",
                                request.getRequestURI(),
                                ex);

                return construirRespuesta(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Se produjo un error interno en el servidor",
                                request,
                                Map.of());
        }

        // Construcción centralizada de las respuestas
        private ResponseEntity<ErrorResponse> construirRespuesta(
                        HttpStatus status,
                        String mensaje,
                        HttpServletRequest request,
                        Map<String, String> detalles) {

                ErrorResponse respuesta = new ErrorResponse(
                                OffsetDateTime.now(ZONA_HORARIA),
                                status.value(),
                                status.getReasonPhrase(),
                                mensaje,
                                request.getRequestURI(),
                                detalles);

                return ResponseEntity.status(status).body(respuesta);
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> manejarCredencialesIncorrectas(
                        BadCredentialsException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.UNAUTHORIZED,
                                "Credenciales incorrectas",
                                request,
                                Map.of());
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> manejarAccesoDenegado(
                        AccessDeniedException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.FORBIDDEN,
                                "No tiene permisos para realizar esta operación",
                                request,
                                Map.of());
        }
}
