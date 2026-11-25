/* Dato che interagire direttamente con TaskEntity può risultare complesso, creiamo questa interfaccia
   che nasconde la logica di accesso ai dati.
   Estendendo JpaRepository, ereditiamo automaticamente tutti i metodi standard per leggere,
   salvare, filtrare (anche tramite Query by Example) e impaginare i task nel database senza scrivere query SQL. */
package com.Engineering.prova.persistence;

import com.Engineering.prova.domain.model.Priority;
import com.Engineering.prova.domain.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    List<TaskEntity> findByProgettoId(Long idProgetto); // trova il progetto attraverso il suo ID

    List<TaskEntity> findByStato(TaskStatus stato); //trova il progetto in base al suo stato (vedi TaskStatus)

    List<TaskEntity> findByPriorita(Priority priorita); //trova il progetto in base alla sua priorità (vedi Priority)

    List<TaskEntity> findByTitoloContainingIgnoreCase(String titolo); //Trova il progetto in base al suo titolo. Ignora la differenza tra caratteri maiuscoli e minuscoli

    List<TaskEntity> findByProgettoCodiceIgnoreCase(String code); //trova il progetto in base al codice Ignora la differenza tra caratteri maiuscoli e minuscoli
}