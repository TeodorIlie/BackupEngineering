package com.Engineering.prova.domain.service;

import com.Engineering.prova.api.dto.CreateTaskRequest;
import com.Engineering.prova.api.dto.TaskResponse;
import com.Engineering.prova.api.dto.UpdateTaskRequest;
import com.Engineering.prova.domain.exception.TaskNotFoundException;
import com.Engineering.prova.domain.model.Priority;
import com.Engineering.prova.domain.model.TaskStatus;
import com.Engineering.prova.persistence.ProjectEntity;
import com.Engineering.prova.persistence.ProjectRepository;
import com.Engineering.prova.persistence.TaskEntity;
import com.Engineering.prova.persistence.TaskRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("all")
public class TaskService {
    //In questo caso, creiamo un archivio dei repository e dei task
    private final TaskRepository archivioTask;
    private final ProjectRepository archivioProgetti;
    // Costruttore: consente di ottenere i dati dell'archivio e dei task dal DB
    public TaskService(TaskRepository archivioTask, ProjectRepository archivioProgetti) {
        this.archivioTask = archivioTask;
        this.archivioProgetti = archivioProgetti;
    }

    //Processo per creare il task
    @Transactional
    public TaskResponse creaTask(CreateTaskRequest req) {
        Optional<ProjectEntity> progettoTrovato = this.archivioProgetti.findById(req.getIdProgetto());
        if (progettoTrovato.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Progetto con id " + req.getIdProgetto() + " non trovato"
            );
        }

        TaskEntity task = new TaskEntity();// si crea l'entità
        task.setProgetto(progettoTrovato.get());//si istanzia l'id del progetto
        task.setTitolo(req.getTitolo());//si applica il titolo
        task.setDescrizione(req.getDescrizione());//si applica la descrizione
        task.setStato(req.getStato() != null ? req.getStato() : TaskStatus.TODO);//verifica se lo stato sia stato instaziato. Se lo stato esiste allora si applica il valore enume to do per avvertire che sia pronto per l'esecuzione
        task.setPriorita(req.getPriorita() != null ? req.getPriorita() : Priority.MEDIUM);//si applica la priorità. Qualora l'utente non espliciti, la priorità è medium
        task.setScadenza(req.getScadenza());// si applica la scadenza
// id, creatoIl e aggiornatoIl vengono generati automaticamente dal database
// una volta creato, va salvato sul database e restituito in risposta
        TaskEntity taskSalvato = this.archivioTask.save(task);//consente di salvare il task nel DB
        return this.inRisposta(taskSalvato);
    }

    // Ricerca avanzata con filtri combinabili (idProgetto, stato, priorita) e paginazione ordinata per id DESC
    @Transactional(readOnly = true)
    public List<TaskResponse> cercaTaskPaginati(Long idProgetto, TaskStatus stato, Priority priorita, int page, int size) {
        // Controlliamo che i parametri di paginazione siano validi; altrimenti lanciamo IllegalArgumentException (errore 400 Bad Request)
        if (page < 0) {
            throw new IllegalArgumentException("Il parametro 'page' deve essere maggiore o uguale a 0");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Il parametro 'size' deve essere compreso tra 1 e 100");
        }
        /*Immaginiamo che l'utente voglia vedere solo una parte dei Task.
         Potrei voler vedere solo i task che hanno la stessa priorità , oppure i task che sono associati allo stesso processo.
         Non avrebbe quindi senso elencare tutti i parametri del task in questo caso.
         L'utente quindi inserisce nella sua ricerca solo un certo numero di parametri.
         L'entità sonda e progetto filtro permettono di creare istruzioni (tradotte poi in SQL), che permettano la rappresentazione solo dei parametri che l'utente ha inserito.
         I campi che risultano vuoti, non verranno visualizzati nella ricerca finale.*/
        TaskEntity sonda = new TaskEntity();
        if (idProgetto != null) {
            ProjectEntity progettoFiltro = new ProjectEntity();
            progettoFiltro.setId(idProgetto);
            sonda.setProgetto(progettoFiltro);
        }
        sonda.setStato(stato);
        sonda.setPriorita(priorita);

        // Il risultato della ricerca dovrà essere ordinato in modo decrescente in base all'ID
        Pageable paginazione = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        // Eseguiamo la ricerca paginata sul DB usando la sonda e convertiamo i risultati in TaskResponse con un ciclo for
        Page<TaskEntity> paginaTask = this.archivioTask.findAll(Example.of(sonda), paginazione);
        List<TaskResponse> risposte = new ArrayList<>();
        for (TaskEntity task : paginaTask.getContent()) {
            risposte.add(this.inRisposta(task));
        }
        return risposte;
    }

    // questa funzione restituisce la lista completa dei task in esecuzione, con i rispettivi parametri
    @Transactional(readOnly = true)
    public List<TaskResponse> trovaTutti() {
        List<TaskResponse> risposte = new ArrayList<>();
        for (TaskEntity task : this.archivioTask.findAll()) {
            risposte.add(this.inRisposta(task));
        }
        return risposte;
    }

    // questa funzione restituisce i parametri della funzione sfruttando l'id della funzione
    @Transactional(readOnly = true)
    public TaskResponse trovaPerId(Long id) {
        return this.inRisposta(this.cercaTask(id));
    }

    // Restituisce un task in funzione del ID del progetto. Qualora il progetto non esistesse, restituisce un'eccezione che avvisa l'utente della manzanza del progetto
    @Transactional(readOnly = true)
    public List<TaskResponse> trovaPerProgetto(Long idProgetto) {
        if (!this.archivioProgetti.existsById(idProgetto)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Progetto con id " + idProgetto + " non trovato"
            );
        }
//usando la funzione inRisposta descritta sotto, e la funzione cercaTask, restituisce in uscita tutti i parametri aggiornati della funzione
        List<TaskResponse> risposte = new ArrayList<>();
        for (TaskEntity task : this.archivioTask.findByProgettoId(idProgetto)) {
            risposte.add(this.inRisposta(task));
        }
        return risposte;
    }
    // la funzione riceve in ingresso uno stato del processo, vedi enum TaskStatus. In base a quello, restituisce tutti i task che si trovano in quel determinato stato
    @Transactional(readOnly = true)
    public List<TaskResponse> trovaPerStato(TaskStatus stato) {
        List<TaskResponse> risposte = new ArrayList<>();
        for (TaskEntity task : this.archivioTask.findByStato(stato)) {
            risposte.add(this.inRisposta(task));
        }
        return risposte;
    }
    // la funzione riceve una priorità, vedi enum Priority. Questa funzione restituisce tutti i task che si trovano in una determinata condizione di priorità
    @Transactional(readOnly = true)
    public List<TaskResponse> trovaPerPriorita(Priority priorita) {
        List<TaskResponse> risposte = new ArrayList<>();
        for (TaskEntity task : this.archivioTask.findByPriorita(priorita)) {
            risposte.add(this.inRisposta(task));
        }
        return risposte;
    }
    //la funzione ricerca il task o i task in base al loro titolo. Non tiene conto della differenza tra le maiuscolo e le minuscole. Restituisce tutti i task con lo stesso nome
    @Transactional(readOnly = true)
    public List<TaskResponse> trovaPerTitolo(String titolo) {
        List<TaskResponse> risposte = new ArrayList<>();
        for (TaskEntity task : this.archivioTask.findByTitoloContainingIgnoreCase(titolo)) {
            risposte.add(this.inRisposta(task));
        }
        return risposte;
    }
    // per aggiornare i parametri di un task in base a quello che l'utente fornisce.
    @Transactional
    public TaskResponse aggiornaTask(Long id, UpdateTaskRequest req) {
        TaskEntity task = this.cercaTask(id);
        task.setTitolo(req.getTitolo());
        task.setDescrizione(req.getDescrizione());
        task.setStato(req.getStato());
        task.setPriorita(req.getPriorita());
        task.setScadenza(req.getScadenza());
        TaskEntity taskAggiornato = this.archivioTask.save(task);
        return this.inRisposta(taskAggiornato);
    }

    // elimina un elemento tramite la funzione cercaTask
    @Transactional
    public void eliminaTask(Long id) {
        TaskEntity task = this.cercaTask(id);
        this.archivioTask.delete(task);
    }

    // questa fuziione cerca un task in base all'id che gli diamo in ingresso.
// semmplicemente, si usa una ricerca sul database. Quando trova un elemento con id coincidente, lo restituisce
    private TaskEntity cercaTask(Long id) {
        Optional<TaskEntity> taskTrovato = this.archivioTask.findById(id);
        if (taskTrovato.isEmpty()) {
            throw new TaskNotFoundException(id);
        }
        return taskTrovato.get();
    }

    // Questa funzione restituisce tutti i parametri di un task in ingressso.
    private TaskResponse inRisposta(TaskEntity t) {
        return new TaskResponse(
                t.getId(), t.getProgetto().getId(), t.getTitolo(), t.getDescrizione(),
                t.getStato(), t.getPriorita(), t.getScadenza(), t.getCreatoIl(), t.getAggiornatoIl()
        );
    }
}