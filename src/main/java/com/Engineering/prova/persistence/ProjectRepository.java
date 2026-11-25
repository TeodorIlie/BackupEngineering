/* Dato che può essere difficile dover lavorare direttamente con ProjectEntity direttamente, possiamo creare un'interfaccia che nasconda la sua complessità.
 Per questo motivo, abbiamo ProjectRepository. Non c'interessa come il metodo esegue un'operazione, ma solo il suo risultato.
 Possiamo quindi chiamare un metodo, senza sapere com'è fatto, inserendo solo i dati e senza query SQL*/
package com.Engineering.prova.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

//la chiamata consente all'interfaccia di ereditare tutti i metodi di ProjectEntity
//Basterà solo inserire l'id del progetto e potremo agire su questo chiamando i metodi senza sapere come siano fatti
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

/*
Questo metodo, che restituisce un valore true o false, è stato implementato per evitare conflitti.
Se due progetti avessero lo stesso codice, nonostante gli id diversi, il sistema non funzionerebbe più correttamente.
Di conseguenza, non possiamo crea un progetto diverso con lo stesso codice.
Questo metodo evita quindi tutto ciò.
*/
    boolean existsByCodiceIgnoreCase(String codice);
}