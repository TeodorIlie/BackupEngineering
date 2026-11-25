/*In questa classe andremo a verificare il funzionamento del nostro sistema usando Springboot
 Inoltre, non verrà usato PostgreSQL, ma useremo un database di nome H2 salvato in memoria
 I processi e i servizi che andremo a creare saranno reali.
 */
package com.Engineering.prova;

import com.Engineering.prova.api.dto.CreateProjectRequest;
import com.Engineering.prova.api.dto.CreateTaskRequest;
import com.Engineering.prova.api.dto.ProjectResponse;
import com.Engineering.prova.api.dto.TaskResponse;
import com.Engineering.prova.domain.service.ProjectService;
import com.Engineering.prova.domain.service.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SuppressWarnings("all")// per evitare falsi allarmi da parte dell'IDE
@SpringBootTest
@ActiveProfiles("test")
// In questo caso, per eseguire il test, usiamo effettivamenìte Springboot (im modalità test)
// Il database in questo caso è reale e viene nominato H2. Non viene usato PostGRE sql
class IntegrazioneFlussoCompletoTest {

    @Autowired
    private ProjectService projectService; // Project Service REALE (non finto!)

    @Autowired
    private TaskService taskService; // Task Service REALE (non finto!)

    @Test
    void flussoCompletoCreaProgettoETaskSuDbH2() {
        // Creiamo un progetto vero sul database H2 in memoria
        // In questo caso il viene prima creata una richiesta tramite costruttore
        // la richiesta viene associata al progetto reale tramite Project Service
        // Infine, si verifica che il progetto abbia un ID
        CreateProjectRequest reqProgetto = new CreateProjectRequest("INT-01", "Progetto Integrazione");
        ProjectResponse progettoSalvato = projectService.createProject(reqProgetto);
        Assertions.assertNotNull(progettoSalvato.getId());

        // Creiamo un task vero collegato all'ID di quel progetto appena salvato
        // Anche in questo caso, viene prima creata una richiesta che poi viene associata all'istanza di un task reale
        // si verfica infine che l'id associato al task non sia nullo
        CreateTaskRequest reqTask = new CreateTaskRequest(progettoSalvato.getId(), "Task reale su H2", "Desc", null, null, null);
        TaskResponse taskSalvato = taskService.creaTask(reqTask);
        Assertions.assertNotNull(taskSalvato.getId());

        // A questo punto, il programma andrà a scorrere la lista dei task associata al progetto da noi creato
        // verificherà che ci sia un solo elemento con un determinato id
        // dato che abbiamo creato il progetto, e subito dopo il task, ci aspettiamo che il task sia il primo del progetto
        // quindi andremo a verificare il primo elemento disponibile, e verificheremo che il nome del task sia quello dichiarato al momento della creazione.
        List<TaskResponse> lista = taskService.trovaPerProgetto(progettoSalvato.getId());
        Assertions.assertEquals(1, lista.size());
        Assertions.assertEquals("Task reale su H2", lista.get(0).getTitolo());
    }
}