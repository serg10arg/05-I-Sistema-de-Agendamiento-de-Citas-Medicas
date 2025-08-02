package com.example.citasmedicas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing // Habilita la auditoría automática para entidades
@EnableAsync // Habilita el soporte para métodos asíncronos
public class CitasmedicasApplication {

    public static void main(String[] args) {
        SpringApplication.run(CitasmedicasApplication.class, args);
    }

}
