/*Possiamo anche in questo caso simulare il comportamento del Project Controller attraverso JUnite e Mock.
Prima di tutto, includiamo i package e le librerie utili allo scopo */
package com.Engineering.prova;

import com.Engineering.prova.api.controller.ProjectController;
import com.Engineering.prova.api.dto.ProjectResponse;
import com.Engineering.prova.domain.service.ProjectService;
import com.Engineering.prova.domain.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("all") //Per evitare falsi allarmi da parte del compilatore
@WebMvcTest(ProjectController.class)//Permette di creare un MVC (model view controller) capace di simulare una pagina web
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc; // Controller MVC mock per simulare le chiamate HTTP POST, GET ecc..

    @MockitoBean
    private ProjectService gestoreProgetti; // Contenitore dove vengono salvati progetti e i metodi con cui agire su di essi

    @MockitoBean
    private TaskService gestoreTask; // Contenitore dove vengono salvati task e i metodi con cui agire su di essi

    // Creazione valida di un progetto
    @Test
    void creaProgettoOk() throws Exception { // viene creato un progetto con id, codice, nome e timestamp
        ProjectResponse finto = new ProjectResponse(1L, "PRJ-01", "Portale Web", Instant.now());
        when(gestoreProgetti.createProject(any())).thenReturn(finto);// questa istruzione obbliga il test a non usare o salvare dati dal DB reale, ma di restituire direttamente i dati registrati al controller MVC

        // prima di trasmettere i dati del progetto, è neccessario convertirli in in formato JSON
        String json = "{\"codiceP\": \"PRJ-01\", \"nome\": \"Portale Web\"}";

        // il mock mvc simulerà quindi il salvataggio del JSON con il rispettivo messaggio 200 ok
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    // In questo specifico caso, il JSON è vuoto.
    // di conseguenza, il sistema restituisce erorre 400, in quanto il controller MVC non può salvare un file JSON vuoto.
    @Test
    void creaProgettoVuotoErrore400() throws Exception {
        String jsonVuoto = "{\"codiceP\": \"\", \"nome\": \"\"}";

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonVuoto))
                .andExpect(status().isBadRequest());
    }

    // In questo caso, vado a creare una funzione che legga tutti i progetti presenti.
    // Creiamo una variabile project response dove salvare temporaneamente i Progetti letti.
    // Questi progetti non vanno presi dal DB, ma dalla Repository virtuale che abbiamo creato.
    // Infine, mockmvc simulerà la chiamata GET alla cartella indicata, con restituzione del codice 200
    @Test
    void leggiTuttiIProgettiOk() throws Exception {
        ProjectResponse p1 = new ProjectResponse(1L, "PRJ-01", "Progetto Uno", Instant.now());
        when(gestoreProgetti.getAllProjects()).thenReturn(List.of(p1));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk());
    }

    // In questo caso, cerchiamo i task di un progetto con id pari a 99 che non esiste.
    // Dato che il progetto è inesistente, viene restituita un'eccezione 404
    // infine, il mock mvc simulerà la risposta del web con annesso l'errore identificativo dell'eccezione
    @Test
    void leggiTaskProgettoInesistenteErrore404() throws Exception {
        when(gestoreTask.trovaPerProgetto(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/projects/99/tasks"))
                .andExpect(status().isNotFound());
    }
}