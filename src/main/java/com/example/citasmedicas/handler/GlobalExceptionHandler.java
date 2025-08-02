package com.example.citasmedicas.handler;

import com.example.citasmedicas.dto.RespuestaError;
import com.example.citasmedicas.excepciones.AccesoDenegadoExcepcion;
import com.example.citasmedicas.excepciones.ConflictoHorarioExcepcion;
import com.example.citasmedicas.excepciones.RecursoNoEncontradoExcepcion;
import com.example.citasmedicas.excepciones.SolicitudInvalidaExcepcion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la API REST.
 * Intercepta excepciones y devuelve respuestas de error estandarizadas.
 */
@ControllerAdvice // Habilita el manejo global de excepciones
public class GlobalExceptionHandler {

    // Logger para registrar las excepciones
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja RecursoNoEncontradoExcepcion (HTTP 404).
     * @param ex La excepción RecursoNoEncontradoExcepcion.
     * @param request La solicitud web.
     * @return ResponseEntity con la RespuestaError.
     */
    @ExceptionHandler(RecursoNoEncontradoExcepcion.class)
    public ResponseEntity<RespuestaError> manejarRecursoNoEncontrado(
            RecursoNoEncontradoExcepcion ex, WebRequest request) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return buildErrorResponse(ex, "RECURSO_NO_ENCONTRADO", ex.getMessage(), null, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja SolicitudInvalidaExcepcion (HTTP 400 Bad Request).
     * @param ex La excepción SolicitudInvalidaExcepcion.
     * @param request La solicitud web.
     * @return ResponseEntity con la RespuestaError.
     */
    @ExceptionHandler(SolicitudInvalidaExcepcion.class)
    public ResponseEntity<RespuestaError> manejarSolicitudInvalida(
            SolicitudInvalidaExcepcion ex, WebRequest request) {
        log.warn("Solicitud inválida: {}", ex.getMessage());
        return buildErrorResponse(ex, "SOLICITUD_INVALIDA", ex.getMessage(), null, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja ConflictoHorarioExcepcion (HTTP 409 Conflict).
     * @param ex La excepción ConflictoHorarioExcepcion.
     * @param request La solicitud web.
     * @return ResponseEntity con la RespuestaError.
     */
    @ExceptionHandler(ConflictoHorarioExcepcion.class)
    public ResponseEntity<RespuestaError> manejarConflictoHorario(
            ConflictoHorarioExcepcion ex, WebRequest request) {
        log.warn("Conflicto de negocio: {}", ex.getMessage());
        return buildErrorResponse(ex, "CONFLICTO_DE_NEGOCIO", ex.getMessage(), null, HttpStatus.CONFLICT);
    }

    /**
     * Maneja AccesoDenegadoExcepcion (HTTP 403 Forbidden).
     * @param ex La excepción AccesoDenegadoExcepcion.
     * @param request La solicitud web.
     * @return ResponseEntity con la RespuestaError.
     */
    @ExceptionHandler(AccesoDenegadoExcepcion.class)
    public ResponseEntity<RespuestaError> manejarAccesoDenegado(
            AccesoDenegadoExcepcion ex, WebRequest request) {
        log.warn("Acceso denegado: {} - Detalles: {}", ex.getMessage(), ex.getDetalles());
        Map<String, String> detalles = ex.getDetalles() != null ? Map.of("detalle", ex.getDetalles()) : null;
        return buildErrorResponse(ex, ex.getCodigoError(), ex.getMessage(), detalles, HttpStatus.FORBIDDEN);
    }


    /**
     * Maneja MethodArgumentNotValidException para errores de validación de @Valid (HTTP 400 Bad Request).
     * @param ex La excepción MethodArgumentNotValidException.
     * @param request La solicitud web.
     * @return ResponseEntity con la RespuestaError.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> manejarValidacionArgumentos(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));

        log.warn("Error de validación: {}", errores);
        return buildErrorResponse(ex, "VALIDACION_FALLIDA", "Uno o más campos tienen errores de validación.", errores, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja IllegalArgumentException (HTTP 400 Bad Request).
     * Se usa para errores generales de lógica de negocio o validación.
     * @param ex La excepción IllegalArgumentException.
     * @param request La solicitud web.
     * @return ResponseEntity con la RespuestaError.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespuestaError> manejarIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        log.warn("Argumento ilegal: {}", ex.getMessage());
        return buildErrorResponse(ex, "ARGUMENTO_INVALIDO", ex.getMessage(), null, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja cualquier otra excepción no cubierta (HTTP 500 Internal Server Error).
     * @param ex La excepción general.
     * @param request La solicitud web.
     * @return ResponseEntity con la RespuestaError.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> manejarTodasLasExcepciones(
            Exception ex, WebRequest request) {
        // Para errores 500, es crucial registrar el stack trace completo.
        log.error("Ha ocurrido un error interno no controlado", ex);
        Map<String, String> detalles = Map.of("causa", "Contacte al administrador del sistema.");
        return buildErrorResponse(ex, "ERROR_INTERNO_SERVIDOR", "Ha ocurrido un error inesperado.", detalles, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Método auxiliar para construir una respuesta de error estandarizada y consistente.
     */
    private ResponseEntity<RespuestaError> buildErrorResponse(
            Exception ex, String codigo, String mensaje, Map<String, String> detalles, HttpStatus status) {

        RespuestaError respuestaError = new RespuestaError(
                codigo,
                mensaje,
                detalles,
                Instant.now(),
                status.value()
        );
        return new ResponseEntity<>(respuestaError, status);
    }
}
