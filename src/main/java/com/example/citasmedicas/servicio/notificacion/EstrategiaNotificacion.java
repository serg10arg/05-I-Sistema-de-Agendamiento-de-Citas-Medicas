package com.example.citasmedicas.servicio.notificacion;

/**
 * Interfaz para el patrón Strategy de Notificaciones.
 * Permite diferentes implementaciones de envío de notificaciones (e.g., Email, SMS).
 */
public interface EstrategiaNotificacion {
    /**
     * Envía una notificación.
     * @param destinatario El destinatario de la notificación (ej. email, número de teléfono).
     * @param asunto El asunto de la notificación.
     * @param mensaje El cuerpo del mensaje.
     */
    void enviarNotificacion(String destinatario, String asunto, String mensaje);
}

