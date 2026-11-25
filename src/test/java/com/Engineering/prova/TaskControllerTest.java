/*Anche in questo caso, vogliamo verificare il funzionamento di Task Controller, senza accedere al DB o al browser.
 Esattamente come fatto in Project Controller test, dobbiamo creare un unità mock mvc che simuli le chiamate http.
 Inoltre, sarà necessario usare JUnit per i metodi che verranno utilizzati */
package com.Engineering.prova;

import com.Engineering.prova.api.controller.TaskController;
import com.Engineering.prova.api.dto.TaskResponse;
import com.Engineering.prova.domain.exception.TaskNotFoundException;
import com.Engineering.prova.domain.model.Priority;
import com.Engineering.prova.domain.model.TaskStatus;
import com.Engineering.prova.domain.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("all")
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc; // Controller MVC mock per simulare le chiamate HTTP POST, GET ecc..

    @MockitoBean
    private TaskService service; // Contenitore dove vengono salvati task e i metodi con cui agire su di essi

   /*In questo test, andiamo a creare un task inizializzando i suoi valori tramite costruttore*/
    @Test
    void creaTaskOk() throws Exception {
        TaskResponse finto = new TaskResponse(1L, 1L, "Test API", "Desc", TaskStatus.TODO, Priority.MEDIUM, null, Instant.now(), Instant.now());
        when(service.creaTask(any())).thenReturn(finto);// questo serve per restituire il task creato direttamente al programma, bypassando il DB

        String json = "{\"idProgetto\": 1, \"titolo\": \"Test API\"}";// creo il file JSON associato

        mockMvc.perform(post("/api/tasks")//Simula il comportamento di un browser o Postman. Restituisce l'esito, che in questo caso, equivale a 201 created, quindi va a buon fine
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    //In questo caso, il controller MVC mock restituirà errore, in quanto, il JSON che gli ho passato è vuoto.
    @Test
    void creaTaskVuotoErrore400() throws Exception {
        String jsonVuoto = "{\"idProgetto\": null, \"titolo\": \"\"}";

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonVuoto))
                .andExpect(status().isBadRequest());
    }

    //  il test consiste nel leggere i task presenti, attraverso una variabilie temporanea t1
    @Test
    void leggiTuttiITaskOk() throws Exception {
        TaskResponse t1 = new TaskResponse(1L, 1L, "Task 1", "Desc", TaskStatus.TODO, Priority.LOW, null, Instant.now(), Instant.now());// contenitore temporaneo per le variabili, inizializzato dal primo elemento tramite costruttore
        when(service.cercaTaskPaginati(null, null, null, 0, 10)).thenReturn(List.of(t1));// L'istruzione restituisce immediatamente il valore letto, bypassando il DB

        mockMvc.perform(get("/api/tasks"))// il controller mock mvc simula l'esito, restituendo ok in questo caso, dato che non si riscontrano problemi
                .andExpect(status().isOk());
    }

    // il test prende in ingresso un task il cui id è pari a 99
    // dato che non esite un task con id così alto, il controller mvc mock restituisce un'eccezione dove viene specificato che il task non esiste
    @Test
    void leggiTaskInesistenteErrore404() throws Exception {
        when(service.trovaPerId(99L)).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound());
    }

    // il test 5 modifica il task inserito, in questo caso il primo del progetto numero 1.
    // dato che questo task esiste, questo viene aggiornato correttamente.
    @Test
    void aggiornaTaskOk() throws Exception {
        TaskResponse aggiornato = new TaskResponse(1L, 1L, "Nuovo titolo", "Desc", TaskStatus.DONE, Priority.HIGH, null, Instant.now(), Instant.now());// i valori modificati per il task 1 del processo 1
        when(service.aggiornaTask(any(), any())).thenReturn(aggiornato);// restituisce il task immediatamente, senza toccare il DB originale
// file JSON che contiene le informazioni del task
        String json = "{\"titolo\": \"Nuovo titolo\", \"stato\": \"DONE\", \"priorita\": \"HIGH\"}";

        mockMvc.perform(put("/api/tasks/1")// il controller mock mvc, simula la risposta web e restituisce l'ok
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    //In questo caso, il task viene correttamente eliminato
    //IL controller mock MVC si limita a simulare la risposta in uscita in cui afferma che il task è stato correttamente eliminato.
    @Test
    void eliminaTaskOk() throws Exception {
        doNothing().when(service).eliminaTask(1L);//dice al compilatore di non generare eccezioni quando il task verrà eliminato
// il controller mock mvc genera la finta risposta
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }
}