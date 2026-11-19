/* Qui viene definito il controller, ossia l'interfaccia che si frappone fra il project Service ed il project Response
 Come al solito, vengono definite libreie e package neccessari al funzionamento*/
package com.Engineering.prova.api.controller;

import com.Engineering.prova.api.dto.CreateProjectRequest;
import com.Engineering.prova.api.dto.ProjectResponse;
import com.Engineering.prova.domain.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")// la cartella a cui dobbiamo accedere per poter far funzionare il controller
@SuppressWarnings("all")//per evitare che il compilsatore possa dare falsi positivi
public class ProjectController {
// definiamo una variabile service, neccessaria per il funzionamento del controller. Questa riceve in ingresso il servizio ricbiesto dall'utente
    private final ProjectService service;

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