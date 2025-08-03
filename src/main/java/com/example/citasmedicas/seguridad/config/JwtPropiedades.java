package com.example.citasmedicas.seguridad.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "jwt")
@Data
@Validated
public class JwtPropiedades {
    @NotBlank
    private String secret;
    private long expiration = 86400000; // 24 hours
}