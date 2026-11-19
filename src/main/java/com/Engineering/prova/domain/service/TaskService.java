/*Il task service implementa tutte le funzioni che possiamo fare su un task.
 Queste funzioni dipendono dalle classi create precedentemente e dalle entità enum.
 Classi: CreateTaskRequest, Task Response, UpdateTaskRequest
 Enum: Priority, TaskStatus*/
package com.Engineering.prova.domain.service;

import com.Engineering.prova.api.dto.CreateTaskRequest;
import com.Engineering.prova.api.dto.TaskResponse;
import com.Engineering.prova.api.dto.UpdateTaskRequest;
import com.Engineering.prova.domain.exception.TaskNotFoundException;
import com.Engineering.prova.domain.model.Priority;
import com.Engineering.prova.domain.model.Task;
import com.Engineering.prova.domain.model.TaskStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@SuppressWarnings("all")
public class TaskService {
// viene creata un array di entità Task.
// si genera un intero che tenga conto dell'identità di ogni task. Due task diversi non possono avere lo stesso id
    private final List<Task> listaTask = new ArrayList<>();
    private long prossimoId = 1;
//Processo per creare il task
    public TaskResponse creaTask(CreateTaskRequest req) {
        Task task = new Task();// si crea l'entità
        task.setId(this.prossimoId++);// il contatore viene incrementato per evitare duplicati
        task.setIdProgetto(req.getIdProgetto());//si istanzia l'id del progetto
        task.setTitolo(req.getTitolo());//si applica il titolo
        task.setDescrizione(req.getDescrizione());//si applica la descrizione
        task.setStato(req.getStato() != null ? req.getStato() : TaskStatus.TODO);//verifica se lo stato sia stato instaziato. Se lo stato esiste allora si applica il valore enume to do per avvertire che sia pronto per l'esecuzione
        task.setPriorita(req.getPriorita() != null ? req.getPriorita() : Priority.MEDIUM);//si applica la priorità. Qualora l'utente non espliciti, la priorità è medium
        task.setScadenza(req.getScadenza());// si applica la scadenza
        task.setCreatoIl(Instant.now());//timestap d'inizio
        task.setAggiornatoIl(Instant.now());//timestap dell'aggiornamento
// una volta creato, va messo nella lista di Task e restituito in risposta
        this.listaTask.add(task);
        return this.inRisposta(task);
    }
// questa funzione restituisce la lista completa dei task in esecuzione, con i rispettivi parametri
    public List<TaskResponse> trovaTutti() {
        List<TaskResponse> risposte = new ArrayList<>();
        for (Task task : this.listaTask) {
            risposte.add(this.inRisposta(task));
        }
        return risposte;
    }
// questa funzione restituisce i parametri della funzione sfruttando l'id della funzione
    public TaskResponse trovaPerId(Long id) {
        return this.inRisposta(this.cercaTask(id));
    }
//usando la funzione inRisposta descritta sotto, e la funzione cercaTask, restituisce in uscita tutti i parametri aggiornati della funzione
    public TaskResponse aggiornaTask(Long id, UpdateTaskRequest req) {
        Task task = this.cercaTask(id);
        task.setTitolo(req.getTitolo());
        task.setDescrizione(req.getDescrizione());
        task.setStato(req.getStato());
        task.setPriorita(req.getPriorita());
        task.setScadenza(req.getScadenza());
        task.setAggiornatoIl(Instant.now());
        return this.inRisposta(task);
    }
// elimina un elemento tramite la funzione cercaTask
    public void eliminaTask(Long id) {
        Task task = this.cercaTask(id);
        this.listaTask.remove(task);
    }
// questa fuziione cerca un task in base all'id che gli diamo in ingresso.
// semmplicemente, si usa un for eache che scorre tutti gli elementi. Quando trova un elemento con id coincidente, lo restituisce

    private Task cercaTask(Long id) {
        for (Task task : this.listaTask) {
            if (task.getId().equals(id)) {
                return task;
            }
        }
        throw new TaskNotFoundException(id);
    }
// Questa funzione restituisce tutti i parametri di un task in ingressso.
    private TaskResponse inRisposta(Task t) {
        return new TaskResponse(
                t.getId(), t.getIdProgetto(), t.getTitolo(), t.getDescrizione(),
                t.getStato(), t.getPriorita(), t.getScadenza(), t.getCreatoIl(), t.getAggiornatoIl()
        );
    }
}
