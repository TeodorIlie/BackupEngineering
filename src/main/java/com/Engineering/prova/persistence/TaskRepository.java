/*Come nel caso di ProjectRepository, vogliamo creare un'interfaccia per i task.
 Vogliamo usare i metodi di TaskEntity senza sapere come siano fatti.*/
package com.Engineering.prova.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

//L'interfaccia eredita i metodi della classe. Basterà semplicemente definire l'id del task, e potremo modificarlo con i metodi implementati, senza sapere come siano fatti
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
}
