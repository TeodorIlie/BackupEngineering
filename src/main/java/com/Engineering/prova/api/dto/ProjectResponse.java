/*
   Questa classe rappresenta i dati restituiti all'utente tramite i metodi get.
   La libreria Jackson traduce automaticamente l'oggetto Java ProjectResponse (creato dal ProjectService
   e restituito dal Controller) in formato JSON nella risposta HTTP.
   Il formato JSON permette al client di ricevere e leggere i dati indipendentemente dal linguaggio
   di programmazione o dal sistema operativo utilizzato.
 */
/* Includiamo il package e le librerie necessarie.
   Questa classe rappresenta la risposta in formato JSON che viene inviata al client
   quando viene creato o letto un progetto (DTO di uscita). */

package com.Engineering.prova.api.dto;

import java.time.Instant;

public class ProjectResponse {

    // Dichiariamo i campi privati e finali (immutabili una volta creato l'oggetto)
    private final Long id;
    private final String CodiceOrdine;
    private final String NomeUtente;
    private final Instant timestamp;

    // Costruttore: inizializza tutti i campi della risposta con i dati del progetto salvato
    public ProjectResponse(Long id, String codiceOrdine, String nomeUtente, Instant timestamp) {
        this.id = id;
        this.CodiceOrdine = codiceOrdine;
        this.NomeUtente = nomeUtente;
        this.timestamp = timestamp;
    }

    // Metodi getter: permettono a Jackson di leggere i campi e trasformarli in JSON
    public Long getId() {
        return this.id;
    }

    public String getCodiceOrdine() {
        return this.CodiceOrdine;
    }

    public String getNomeUtente() {
        return this.NomeUtente;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }
}