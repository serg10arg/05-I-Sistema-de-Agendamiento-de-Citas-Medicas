package com.example.citasmedicas.servicio.notificacion;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Implementación de EstrategiaNotificacion para envío de correos electrónicos.
 * (Funcionalidad simulada para el propósito del proyecto).
 */
@Primary // Indica a Spring que esta es la implementación por defecto de EstrategiaNotificacion
@Component
public class EmailNotificacionEstrategia implements EstrategiaNotificacion {
    @Override
    public void enviarNotificacion(String destinatario, String asunto, String mensaje) {
        // Lógica real de envío de email aquí (e.g., con JavaMailSender)
        System.out.println("Enviando EMAIL a: " + destinatario);
        System.out.println("Asunto: " + asunto);
        System.out.println("Mensaje: " + mensaje);
        System.out.println("--- EMAIL ENVIADO ---");
    }
}
