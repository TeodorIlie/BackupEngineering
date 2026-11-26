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

    @Bean// la funzione restituisce un oggetto di tipo OpenAI che contiene il nome, la descrizione e la pagina a cui collegarsi per fare riferimento al nostro progetto
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Project & Task Management API")// Titolo del'API OpenAI
                        .version("1.0.0")// Versione dell'API
                        .description("API REST per la gestione di progetti e task con Spring Boot 4, PostgreSQL e Flyway.")//Descrizione del risultato e del funzionamento del programma e dell'API
                        .contact(new Contact()
                                .name("Teodor Ilie")//Autore del programma
                                .url("https://github.com/TeodorIlie/BackupEngineering")));//link del progetto
    }
}