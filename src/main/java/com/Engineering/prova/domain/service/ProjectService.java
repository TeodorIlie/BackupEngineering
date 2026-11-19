/*Includiamo come al solito i package e le librerie neccessarie.
 In questo caso, project service restituisce i dati del progetto in questione, in base al nome e al codice del progetto stesso.
 Il risultato sarà poi passato a Project Response e infine, all'utente in formato JSON*/

package com.Engineering.prova.domain.service;

import com.Engineering.prova.api.dto.CreateProjectRequest;
import com.Engineering.prova.api.dto.ProjectResponse;
import com.Engineering.prova.domain.model.Project;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@SuppressWarnings("all")// Per evitare falsi allarmi da parte del compilatore
public class ProjectService {

    /*Definiamo un array di progetti, ed un intero di nome nextid, in modo che due progetti diversi non possano avere lo stesso id. */
    private final List<Project> projects = new ArrayList<>();

    private long nextId = 1;

    /*a questo punto, definiamo una funzione che crea il progetto e lo resistuisce all'utente.
    Questa classe, riceve in ingresso una richiesta dei dati, prende i dati associati in base al nome utente e al codice del progetto.
    Dopodiché la funzione restituisce prima il dato al Project Response, il quale restituisce sottoforma di JSON il dato finale all'utente.*/
    public ProjectResponse createProject(CreateProjectRequest request) {
        Project project = new Project(
                this.nextId,
                request.getCode(),
                request.getName(),
                Instant.now()
        );
        this.nextId++;

        this.projects.add(project);

        return new ProjectResponse(
                project.getId(),
                project.getCode(),
                project.getNomeUtente(),
                project.getTimestamp()
        );
    }
    /* Questo metodo, usa una struttura ad iteratore per scorrrere tutti gli elementi dell'array dei Progetti
    In modo da trovare le corrispondenze in base al nome ed al codice. Infine, restituisce le corrispondenze.*/
    public List<ProjectResponse> getAllProjects() {
        List<ProjectResponse> responses = new ArrayList<>();

        for (Project project : this.projects) {
            ProjectResponse response = new ProjectResponse(
                    project.getId(),
                    project.getCode(),
                    project.getNomeUtente(),
                    project.getTimestamp()
            );
            responses.add(response);
        }

        return responses;
    }
}

