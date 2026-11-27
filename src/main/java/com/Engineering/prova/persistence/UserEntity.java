/* Dato che abbiamo creato uno script per definire ruoli, accessi e password, vogliamo creare una classe che acceda alla relazione senza toccare codice SQL.
 Come al solito partiamo dai package e dalle librerie utili allo scopo*/
package com.Engineering.prova.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity//Creiamo una tabella che includa di defaul i metodi get e set per accedere alle variabili
@Table(name = "users")//si collega alla tabella definita nello script v4
@Getter//include di default i metodi get
@Setter//include di default i metodi set
@NoArgsConstructor//include di default il costruttore privo di argomenti
public class UserEntity {

    @Id// attributo che si collega alla chiave primaria della relazione users
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;// attributo che si collega alla relazione users. Permette di modificare lo username. Dev'essere unico, non nullo, e avere non più di 50 caratteri

    @Column(name = "password", nullable = false, length = 255)
    private String password;// attributo che si collega alla relazione users. Permette di modificare la password. Non dev'essere nullo, e avere non più di 255 caratteri

    @Column(name = "role", nullable = false, length = 30)
    private String role;// permette di modificare il ruolo dell'utente. Non può essere vuoto, e dev'essere lungo non più di 30 caratteri
}