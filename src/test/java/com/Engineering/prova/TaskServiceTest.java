/*Questa classe è stata creata con l'idea di eseguire un test per il funzionamento corretto di TaskService
 Anche in questo caso, verranno impiegato Mockito e JUnit (vedi ProjectServiceTest).
 Per iniziare, includiamo i package e le librerie per il funzionamento del programma.
 */
package com.Engineering.prova;

import com.Engineering.prova.api.dto.CreateTaskRequest;
import com.Engineering.prova.api.dto.TaskResponse;
import com.Engineering.prova.api.dto.UpdateTaskRequest;
import com.Engineering.prova.domain.exception.TaskNotFoundException;
import com.Engineering.prova.domain.model.Priority;
import com.Engineering.prova.domain.model.TaskStatus;
import com.Engineering.prova.domain.service.TaskService;
import com.Engineering.prova.persistence.ProjectEntity;
import com.Engineering.prova.persistence.ProjectRepository;
import com.Engineering.prova.persistence.TaskEntity;
import com.Engineering.prova.persistence.TaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SuppressWarnings("all") //per evitare i falsi positivi da parte del compilatore
@ExtendWith(MockitoExtension.class) //Includiamo i mock, ossia le entità "fasulle" per i test, senza nuovamente dover accedere al DB
class TaskServiceTest {
    // creo due entità mock: una per i task, ed una per i progetti, che rappresenatno i contenitori dove metteremo i nostri dati finti
    @Mock
    private TaskRepository archivioTask;

    @Mock
    private ProjectRepository archivioProgetti;

    // Mettiamo l'archivio progetti e task all'interno dell'enità task service finta
    @InjectMocks
    private TaskService taskService;

    @Test
    void creaTaskOk() {
        // Prepariamo il progetto finto esistente sul DB con ID 1
        ProjectEntity progetto = new ProjectEntity();
        progetto.setId(1L);

        // Creiamo un task con id progetto, titolo e descrizione inizializzate, mentre tutti gli altri campi rimangono vuoti
        CreateTaskRequest richiesta = new CreateTaskRequest(1L, "Scrivere i test", "Unit test service", null, null, null);

        // Creiamo un nuovo Task, con id del task pari a 10, e riempiamo tutti gli altri campi con i dati rimanenti (priorità, stato ecc..)
        TaskEntity salvato = new TaskEntity();
        salvato.setId(10L);
        salvato.setProgetto(progetto);
        salvato.setTitolo("Scrivere i test");
        salvato.setDescrizione("Unit test service");
        salvato.setStato(TaskStatus.TODO);
        salvato.setPriorita(Priority.MEDIUM);

        // JUnit cercherà un progetto il cui ID del progetto valga 1. In quel caso, tutti gli spazi che non verranno riempiti, verranno assegnati con i valori di default del progetto salvato
        when(archivioProgetti.findById(1L)).thenReturn(Optional.of(progetto));
        when(archivioTask.save(any())).thenReturn(salvato);

        // prende in ingresso il task appena creato, e restituisce una risposta
        TaskResponse risposta = taskService.creaTask(richiesta);

        // la risposta generata precedentemente viene inserita nelle funzioni assert per essere confrontata con i valori che ci aspettiamo dal precedente inserimento.
        Assertions.assertEquals(10L, risposta.getId());
        Assertions.assertEquals(1L, risposta.getIdProgetto());
        Assertions.assertEquals("Scrivere i test", risposta.getTitolo());
        Assertions.assertEquals(TaskStatus.TODO, risposta.getStato());
        Assertions.assertEquals(Priority.MEDIUM, risposta.getPriorita());
    }

    @Test
    void creaTaskProgettoNonTrovatoErrore404() {
        // In questo caso, viene creato un task con ID pari a 99
        CreateTaskRequest richiesta = new CreateTaskRequest(99L, "Task orfano", "Desc", null, null, null);

        // In questo caso, la condizione da valutare è l'esistenza di un progetto con indice pari a 99
        when(archivioProgetti.findById(99L)).thenReturn(Optional.empty());

        // Il compilatore tenterà di creare tale task, ma fallirà, in quanto l'indice associato al task (ossia del progetto), non esiste nel DB.
        try {
            taskService.creaTask(richiesta);
            Assertions.fail("Doveva lanciare un errore 404!");
        } catch (ResponseStatusException errore) {
            // Di conseguenza, il compilatore non potrà associare alcun progetto a questo task, e lancerà di conseguenza 404
            Assertions.assertEquals(HttpStatus.NOT_FOUND, errore.getStatusCode());
        }
    }

    @Test
    void trovaPerIdNonTrovatoLanciaEccezione() {
        // In questo caso, inseriamo un ID del Task che non è però mai stato creato
        when(archivioTask.findById(50L)).thenReturn(Optional.empty());

        // l'ID del task non è associato ad alcun task esistente.
        // in conseguenza di questo, quando lanceremo la funzione per trovare il task attraverso il suo ID, il compilatore restituirà nuovamente errore
        try {
            taskService.trovaPerId(50L);
            Assertions.fail("Doveva lanciare TaskNotFoundException!");
        } catch (TaskNotFoundException errore) {
            Assertions.assertTrue(errore.getMessage().contains("50"));
        }
    }

    @Test
    void aggiornaTaskOk() {
        // In questo caso creiamo un progetto con ID pari a 1
        ProjectEntity progetto = new ProjectEntity();
        progetto.setId(1L);

        // Simuliamo un Task esistente con id pari a 5, appartenente al progetto pari a 1
        TaskEntity esistente = new TaskEntity();
        esistente.setId(5L);
        esistente.setProgetto(progetto);
        esistente.setTitolo("Vecchio titolo");

        // modifichiamo il task numero 5 con i nuovi dati, tra cui titolo, descrizione ecc...
        UpdateTaskRequest modifica = new UpdateTaskRequest("Nuovo titolo", "Nuova desc", TaskStatus.DONE, Priority.HIGH, null);

        // Queste due istruzioni servono per trovare e restituire i valori del task 5 progetto 1
        when(archivioTask.findById(5L)).thenReturn(Optional.of(esistente));
        when(archivioTask.save(any())).thenReturn(esistente);

        // Modifichiamo i valori del task numero 5
        TaskResponse risposta = taskService.aggiornaTask(5L, modifica);

        // Verifichiamo che i campi siano stati aggiornati
        Assertions.assertEquals("Nuovo titolo", risposta.getTitolo());
        Assertions.assertEquals(TaskStatus.DONE, risposta.getStato());
        Assertions.assertEquals(Priority.HIGH, risposta.getPriorita());
    }

    @Test
    void cercaTaskPaginatiParametriNonValidiErrore400() {
        // Testiamo che page negativa (-1) lanci IllegalArgumentException
        try {
            taskService.cercaTaskPaginati(null, null, null, -1, 10);
            Assertions.fail("Doveva lanciare IllegalArgumentException per page < 0!");
        } catch (IllegalArgumentException errore) {
            Assertions.assertNotNull(errore.getMessage());
        }

        // Testiamo che size > 100 lanci IllegalArgumentException
        try {
            taskService.cercaTaskPaginati(null, null, null, 0, 200);
            Assertions.fail("Doveva lanciare IllegalArgumentException per size > 100!");
        } catch (IllegalArgumentException errore) {
            Assertions.assertNotNull(errore.getMessage());
        }
    }

    // In questo caso, creiamo un progetto con indice pari ad 1.
    // Creiamo anche un task con id pari ad 1
    @Test
    void trovaPerProgettoOk() {
        ProjectEntity progetto = new ProjectEntity();
        progetto.setId(1L);

        // vengono definiti ulteriori parametri del task, tra cui il titolo ecc...
        TaskEntity t1 = new TaskEntity();
        t1.setId(1L);
        t1.setProgetto(progetto);
        t1.setTitolo("Task 1");

        // cerca in primo luogo il progetto con ID pari ad 1
        // Una volta trovato il progetto, restituisce i parametri del task t1 creato precedentemente
        when(archivioProgetti.existsById(1L)).thenReturn(true);
        when(archivioTask.findByProgettoId(1L)).thenReturn(List.of(t1));

        // Creiamo una lista di Task, il cui progetto è uguale ad 1
        List<TaskResponse> lista = taskService.trovaPerProgetto(1L);

        // Verifica che la lista restituita abbia esattamente un elemento
        // Assicura che l'elemento restituito sia il primo elemento della lista dei Task, ossia Task1, e che venga restituito con esso il titolo
        Assertions.assertEquals(1, lista.size());
        Assertions.assertEquals("Task 1", lista.get(0).getTitolo());
    }
}