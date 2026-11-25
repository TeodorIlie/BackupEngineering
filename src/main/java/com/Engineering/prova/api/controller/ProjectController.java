/* Qui viene definito il controller, ossia l'interfaccia che si frappone fra il project Service e il project Response
 Come al solito, vengono definite librerie e package necessari al funzionamento*/
package com.Engineering.prova.api.controller;

import com.Engineering.prova.api.dto.CreateProjectRequest;
import com.Engineering.prova.api.dto.ProjectResponse;
import com.Engineering.prova.api.dto.TaskResponse;
import com.Engineering.prova.domain.service.ProjectService;
import com.Engineering.prova.domain.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects") // indirizzo base del controller
@SuppressWarnings("all") // evita falsi avvisi dell'IDE
public class ProjectController {

    // gestore della logica dei progetti
    private final ProjectService gestoreProgetti;
    // gestore della logica dei task
    private final TaskService gestoreTask;

    // il costruttore collega automaticamente i gestori al controller
    public ProjectController(ProjectService gestoreProgetti, TaskService gestoreTask) {
        this.gestoreProgetti = gestoreProgetti;
        this.gestoreTask = gestoreTask;
    }

    // crea un nuovo progetto ricevendo i dati dal client
    @PostMapping
    public ProjectResponse creaProgetto(@Valid @RequestBody CreateProjectRequest datiProgetto) {
        return this.gestoreProgetti.createProject(datiProgetto);
    }

    // restituisce la lista di tutti i progetti salvati
    @GetMapping
    public List<ProjectResponse> leggiTuttiIProgetti() {
        return this.gestoreProgetti.getAllProjects();
    }

    // restituisce tutti i task associati a uno specifico progetto (404 se il progetto non esiste)
    @GetMapping("/{id}/tasks")
    public List<TaskResponse> leggiTaskDelProgetto(@PathVariable Long id) {
        return this.gestoreTask.trovaPerProgetto(id);
    }
}