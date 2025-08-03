package com.example.citasmedicas.controlador;

import com.example.citasmedicas.servicio.ReporteServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Controlador REST para la generación de reportes asíncronos.
 */
@RestController
@RequestMapping("/api/v1/reportes") // Versión de la API
public class ReporteControlador {

    private final ReporteServicio reporteServicio;

    public ReporteControlador(ReporteServicio reporteServicio) {
        this.reporteServicio = reporteServicio;
    }

    /**
     * Inicia la generación asíncrona de un reporte de citas en formato CSV.
     * POST /api/v1/reportes/citas-csv?pacienteId={id}
     * @param pacienteId El ID del paciente para quien se genera el reporte.
     * @return ResponseEntity con un estado 202 Accepted y la URL para consultar el estado del trabajo.
     */
    @PostMapping("/citas-csv")
    public ResponseEntity<String> generarReporteCitasCSV(@RequestParam UUID pacienteId) throws IOException {
        // Crear un archivo temporal para el reporte
        Path rutaArchivo = Paths.get(System.getProperty("java.io.tmpdir"), "reporte_citas_" + UUID.randomUUID() + ".csv");

        // Iniciar la generación del reporte de forma asíncrona
        CompletableFuture<UUID> jobFuturo = reporteServicio.generarReporteCitasPacienteCSV(pacienteId, rutaArchivo.toString());

        // En un sistema real, guardaríamos el 'jobId' y 'rutaArchivo' en una base de datos
        // para que el cliente pudiera consultar el estado y luego descargar el archivo.
        // Aquí solo simulamos la respuesta inicial 202 Accepted.

        // Retornar 202 Accepted con la URL para consultar el estado
        return ResponseEntity.accepted()
                .header("Location", "/api/v1/reportes/estado/" + jobFuturo.join()) // Simulación de Location header
                .body("Reporte de citas en CSV iniciado. Consulte el estado en: /api/v1/reportes/estado/" + jobFuturo.join());
    }

    // Nota: Un endpoint real para "GET /api/v1/reportes/estado/{jobId}" y otro para la descarga
    // serían necesarios para una implementación completa del patrón de operaciones de larga duración.
}

