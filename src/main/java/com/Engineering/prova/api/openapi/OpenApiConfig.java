/*Questa classe viene utilizzata per generare un'interfaccia grafica del nostro codice grazie ad OpenAPI e Swagge.
Swagger ed OpenApi vengono inclusi tramite i package sottostanti .*/
package com.Engineering.prova.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration// Creiamo una classe di configurazione API per poter visualizzare in modo interattivo tutto il nostro codice
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Project & Task Management API")
                        .version("1.0.0")
                        .description("API REST per la gestione di progetti e task con Spring Boot 4, PostgreSQL e Flyway.")
                        .contact(new Contact()
                                .name("Teodor Ilie")
                                .url("https://github.com/TeodorIlie/BackupEngineering")));
    }
}