/*Questa classe serve per testare le funzionalità di ProjectService, senza dover accedere al nostro DB.
In questo caso verranno usate due funzionalità:
- Mockito: che crea un "mock", ossia, una ProjectRepository "fasulla" da usare durante il Test, senza obbligarci ad accedere a entità o metodi reali del DB.
- Junit: l'ambiente di esecuzione e di test automatico. Il motore di esecuzione del test (Junit5 in questo caso)
Come al solito, definiamo i package e le librerie necessarie a far funzionare il nostro programma*/
package com.Engineering.prova;

import com.Engineering.prova.api.dto.CreateProjectRequest;
import com.Engineering.prova.api.dto.ProjectResponse;
import com.Engineering.prova.domain.service.ProjectService;
import com.Engineering.prova.persistence.ProjectEntity;
import com.Engineering.prova.persistence.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)//definiamo la nostra classe "fasulla" per il test
class ProjectServiceTest {

    @Mock
    private ProjectRepository archivioProgetti; // creiamo la nostra projectRepository fasulla

    @InjectMocks
    private ProjectService projectService; // creiamo il nostro projectService fasullo

    @Test
    void creaProgettoOk() {
        CreateProjectRequest richiesta = new CreateProjectRequest("PRJ-01", "Piattaforma"); //Creiamo il nostro progetto di prova impostando codice, ID e nome della Piattaforma in cui è contenuto

        ProjectEntity salvato = new ProjectEntity();
        salvato.setId(1L);
        salvato.setCodice("PRJ-01");
        salvato.setNome("Piattaforma");
        when(archivioProgetti.existsByCodiceIgnoreCase("PRJ-01")).thenReturn(false); // Controlla se esiste un progetto con il codice indicato, ignorando le differenze tra maiuscole e minuscole. Qualora esistesse, la funzione restituisce false
        when(archivioProgetti.save(any())).thenReturn(salvato); //quando la funzione salva qualcosa, restituisce l'unità salvata all'utente
// Creo un DTO di risposta, che confronterà la richiesta di creazione del mio progetto con i dati che gli ho fornito
        ProjectResponse risposta = projectService.createProject(richiesta);
// confronta ì risultati della funzione precedente per verificare se quello che ho inserito corrisponda a ciò che JUnit si aspetta
        assertEquals(1L, risposta.getId());
        assertEquals("PRJ-01", risposta.getCodiceOrdine());
        assertEquals("Piattaforma", risposta.getNomeUtente());
    }
// Test che restituisce codice 409 di conflitto (progetto già esistete)
    @Test
    void creaProgettoDuplicatoErrore409(){
        // JUnit crea una nuova richiesta uguale a quella fatta precedentemente.
        // Ci aspettiamo che questa restituisca codice 409, in quanto non possono esistere due progetti diversi con lo stesso id e codice
        CreateProjectRequest richiesta = new CreateProjectRequest("PRJ-01", "Duplicato");
        when(archivioProgetti.existsByCodiceIgnoreCase("PRJ-01")).thenReturn(true); //Confronta il progetto appena creato con quello creato precedentemente

        try {//verifica la condizione booleana scritta precedentemente, che restituirà true in questo caso, abbinato al messaggio indicato.
            projectService.createProject(richiesta);
            fail("Doveva lanciare un errore 409!");
        } catch (ResponseStatusException errore) {
            //lancia l'eccezione di conflitto in quanto i due progetti presentano lo stesso ID e codice
            assertEquals(HttpStatus.CONFLICT, errore.getStatusCode());
        }
    }

    @Test
    void leggiTuttiIProgettiOk() {
        //Creiamo un DB fasullo, che sia associato al progetto P1
        ProjectEntity p1 = new ProjectEntity();
        //Nome e codice del progetto
        p1.setCodice("PRJ-01");
        p1.setNome("Alpha");
        // Restituisce tutti gli oggetti appartenenti a p1
        when(archivioProgetti.findAll()).thenReturn(List.of(p1));
        //Creiamo una lista di progetti che prenda tutti i progetti a nostra disposizione
        List<ProjectResponse> lista = projectService.getAllProjects();
        //verifica che esista esattamente un progetto con il codice "PRJ-01"
        assertEquals(1, lista.size());
        assertEquals("PRJ-01", lista.get(0).getCodiceOrdine());
    }
}