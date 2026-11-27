/*Questa interfaccia è stata creata per consentire la ricerca facilitata di un utente all'interno del DB, attraverso il suo username */
package com.Engineering.prova.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
// In pratica, la nostra interfaccia eredita i metodi e le caratteristiche della classe JPA, che richiede un'entità utente e la sua chiave primara(id)
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);// Trova l'utente attraverso lo username. Optional ci dice che lo username potrebbe non essere associato a nessun utente, e quindi l'utente cercato potrebbe non esistere.
}