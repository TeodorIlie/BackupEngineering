/* Una volta implementate le classi Task Response, Create Task Request e Update Task Request, possiamo utilizzare un unico controller che sfrutti le funzionalità di tutti e tre insieme.
 Task Controller è quindi l'interfaccia che si collega con le tre classi citate, e che fornisce un'implementazione diretta per l'utente*/
package com.Engineering.prova.api.controller;

import com.Engineering.prova.api.dto.CreateTaskRequest;
import com.Engineering.prova.api.dto.TaskResponse;
import com.Engineering.prova.api.dto.UpdateTaskRequest;
import com.Engineering.prova.domain.model.Priority;
import com.Engineering.prova.domain.model.TaskStatus;
import com.Engineering.prova.domain.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")//indirizzo di accesso del controller
@SuppressWarnings("all")//evita i falsi positivi da parte del compilatore
public class TaskController {
    // Viene inizializzata un'entità TaskService
    private final TaskService service;

    // costruttore del controller, riceve in ingresso un TaskService
    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping// la funzione crea un task e restituisce tramite un'eccezione l'ok della creazione
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse crea(@Valid @RequestBody CreateTaskRequest req) {
        return this.service.creaTask(req);
    }

    @GetMapping // Restituisce i task paginati (ordinati per id DESC) e filtrabili per progetto, stato, priorità o titolo
    public List<TaskResponse> leggiTutti(
            // Parametri opzionali tramite URL per filtrare e paginare i risultati
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String titolo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Se viene cercato per titolo, manteniamo la ricerca indifferente rispetto alle maiuscole o minuscole
        if (titolo != null && !titolo.isBlank()) {
            return this.service.trovaPerTitolo(titolo.trim());
        }

        // Altrimenti deleghiamo al service la ricerca combinata (projectId, status, priority) e paginata
        return this.service.cercaTaskPaginati(projectId, status, priority, page, size);
    }

    @GetMapping("/{id}") // Accede ad un singolo task tramite id
    public TaskResponse leggiUno(@PathVariable Long id) {
        return this.service.trovaPerId(id);
    }

    @PutMapping("/{id}") // Modifica i parametri di un task esistente
    public TaskResponse aggiorna(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest req) {
        return this.service.aggiornaTask(id, req);
    }

    @DeleteMapping("/{id}") // Cancella un task tramite id
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void elimina(@PathVariable Long id) {
        this.service.eliminaTask(id);
    }
}