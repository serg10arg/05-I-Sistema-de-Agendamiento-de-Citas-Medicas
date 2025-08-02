package com.example.citasmedicas.servicio.notificacion;

import org.springframework.stereotype.Component;

/**
 * Implementación de EstrategiaNotificacion para envío de SMS.
 * (Funcionalidad simulada para el propósito del proyecto).
 */
@Component
public class SMSNotificacionEstrategia implements EstrategiaNotificacion {
    @Override
    public void enviarNotificacion(String destinatario, String asunto, String mensaje) {
        // Lógica real de envío de SMS aquí (e.g., con Twilio API)
        System.out.println("Enviando SMS a: " + destinatario);
        System.out.println("Asunto: " + asunto);
        System.out.println("Mensaje: " + mensaje);
        System.out.println("--- SMS ENVIADO ---");
    }
}

