/* Qui viene definito il controller, ossia l'interfaccia che si frappone fra il project Service e il project Response
 Come al solito, vengono definite librerie e package necessari al funzionamento*/
package com.Engineering.prova.api.controller;

import com.Engineering.prova.api.dto.CreateProjectRequest;
import com.Engineering.prova.api.dto.ProjectResponse;
import com.Engineering.prova.domain.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects") // la cartella a cui dobbiamo accedere per poter far funzionare il controller
@SuppressWarnings("all") // per evitare che il compilatore possa dare falsi positivi
public class ProjectController {

    // definiamo una variabile service, necessaria per il funzionamento del controller
    private final ProjectService service;

    // richiamiamo il costruttore affinché la variabile service riceva la richiesta che vogliamo fare al server
    public ProjectController(ProjectService service) {
        this.service = service;
    }

    // si richiama un metodo HTTP POST, che risponde con un progetto che abbiamo richiesto al service
    @PostMapping
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request) {
        return this.service.createProject(request);
    }

    // si usa un metodo HTTP GET per raccogliere tutti i progetti di nostro interesse
    @GetMapping
    public List<ProjectResponse> getAll() {
        return this.service.getAllProjects();
    }
}