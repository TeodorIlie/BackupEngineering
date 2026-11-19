/*
 * Anche qui inseriamo il package e le librerie necessarie al funzionamento.
 * - In particolare, importiamo le annotazioni di Bean Validation per verificare che
 *   i dati inviati dall'utente (codice e nome) non siano vuoti e rispettino i vincoli di lunghezza.
 * - Con questo record definiamo il contratto dei dati in ingresso consentiti.
 */
package com.Engineering.prova.api.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class CreateProjectRequest {

        @NotBlank(message = "Il codice è obbligatorio")
        @Size(min = 3, max = 20, message = "Il codice deve essere tra 3 e 20 caratteri")
        private String code;

        @NotBlank(message = "Il nome è obbligatorio")
        private String name;

        // Costruttore vuoto richiesto da Jackson per deserializzare il JSON in ingresso
        public CreateProjectRequest() {
        }

        public CreateProjectRequest(String code, String name) {
                this.code = code;
                this.name = name;
        }

        public String getCode() {
                return code;
        }

        public void setCode(String code) {
                this.code = code;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }
}