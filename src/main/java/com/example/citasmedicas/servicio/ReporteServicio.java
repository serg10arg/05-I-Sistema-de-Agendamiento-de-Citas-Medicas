package com.example.citasmedicas.servicio;

import com.example.citasmedicas.modelo.entidad.Cita;
import com.example.citasmedicas.modelo.entidad.Paciente;
import com.example.citasmedicas.repositorio.CitaRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture; // Para operaciones asíncronas

/**
 * Servicio para generar reportes, demostrando operaciones asíncronas.
 */
@Service
public class ReporteServicio {

    private static final Logger log = LoggerFactory.getLogger(ReporteServicio.class);
    private final CitaRepositorio citaRepositorio;
    private final PacienteServicio pacienteServicio; // Para obtener la entidad Paciente

    public ReporteServicio(CitaRepositorio citaRepositorio, PacienteServicio pacienteServicio) {
        this.citaRepositorio = citaRepositorio;
        this.pacienteServicio = pacienteServicio;
    }

    /**
     * Simula la generación de un reporte CSV de citas de un paciente de forma asíncrona.
     * @param pacienteId El ID del paciente.
     * @param rutaArchivo La ruta donde se guardará el archivo CSV.
     * @return Un CompletableFuture que contendrá el ID del trabajo del reporte cuando esté listo.
     */
    @Async // Marca el método como asíncrono
    public CompletableFuture<UUID> generarReporteCitasPacienteCSV(UUID pacienteId, String rutaArchivo) {
        UUID jobId = UUID.randomUUID(); // Genera un ID para el trabajo del reporte
        log.info("Iniciando generación de reporte para paciente {} con Job ID: {}", pacienteId, jobId);

        // Simula una operación de larga duración
        try {
            Thread.sleep(5000); // 5 segundos de retraso simulado
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupción al generar reporte", e);
        }

        // Recuperar la entidad del paciente para usarla en la consulta
        Paciente paciente = pacienteServicio.obtenerEntidadPacientePorId(pacienteId);
        List<Cita> citas = citaRepositorio.findByPaciente(paciente, Pageable.unpaged()).getContent();

        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            writer.append("ID Cita,Doctor,Paciente,Hora Inicio,Hora Fin,Razon Visita,Estado\n");
            for (Cita cita : citas) {
                writer.append(String.format("%s,%s %s,%s %s,%s,%s,%s,%s\n",
                        cita.getId().toString(),
                        cita.getDoctor().getPrimerNombre(), cita.getDoctor().getApellido(),
                        cita.getPaciente().getPrimerNombre(), cita.getPaciente().getApellido(),
                        cita.getDisponibilidad().getHoraInicio().toString(),
                        cita.getDisponibilidad().getHoraFin().toString(),
                        cita.getRazonVisita(),
                        cita.getEstado().name()
                ));
            }
            log.info("Reporte CSV para Job ID {} generado exitosamente en: {}", jobId, rutaArchivo);
        } catch (IOException e) {
            log.error("Error al escribir el archivo CSV para Job ID {}: {}", jobId, e.getMessage(), e);
            // Podrías registrar el estado del trabajo como 'FALLIDO' en una base de datos
            throw new RuntimeException("Error al generar el reporte CSV", e);
        }

        // En un sistema real, el estado del job se actualizaría en una base de datos
        // Y el cliente podría sondear /report-jobs/{jobId} para obtener el estado y la URL de descarga
        return CompletableFuture.completedFuture(jobId);
    }
}
