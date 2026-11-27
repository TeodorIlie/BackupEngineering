/*Una volta inseriti i dati, il sistema dovrà verificare la validità di quanto inserito.
In questo file vengono definiti i metodi con cui il sistema carica caricherà i nostri dati, è verificherà che l'utente inserito esista effettivamente nel db */
package com.Engineering.prova.security;

import com.Engineering.prova.persistence.UserEntity;
import com.Engineering.prova.persistence.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository; // Contenitore che contiene i dati degli utenti registrati

    public CustomUserDetailsService(UserRepository userRepository) { // il costruttore richiede l'inserimento dei parametri in ingresso
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> trovato = userRepository.findByUsername(username); // Optional indica che l'utente potrebbe non esistere nel DB

        if (trovato.isEmpty()) {// se la variabile risulta vuota il sistema restituisce errore, in quanto lo username e la password inseriti non risultano di nessun utente registrato nel DB
            throw new UsernameNotFoundException("Utente non trovato: " + username);
        }

        UserEntity user = trovato.get();// nel caso in cui esista un utente, questo viene salvato all'interno della variabile user

        return User.withUsername(user.getUsername())// restituisce i dati dell'utente, tra cui ruolo ecc...
                .password(user.getPassword())
                .authorities(user.getRole())
                .build();
    }
}