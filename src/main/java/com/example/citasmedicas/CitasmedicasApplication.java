package com.example.citasmedicas;

import com.example.citasmedicas.seguridad.config.JwtPropiedades;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing // Habilita la auditoría automática para entidades
@EnableAsync // Habilita el soporte para métodos asíncronos
@EnableConfigurationProperties(JwtPropiedades.class) // Habilita la clase de propiedades JWT
public class CitasmedicasApplication {

    public static void main(String[] args) {
        SpringApplication.run(CitasmedicasApplication.class, args);
    }

}
