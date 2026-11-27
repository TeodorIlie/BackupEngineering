/* A questo punto, una volta stabilite le policy di accesso, i token JWT e la crittografia, definiamo il controller con cui porter inserire i nostri dati
*/
package com.Engineering.prova.web;

import com.Engineering.prova.persistence.UserEntity;
import com.Engineering.prova.persistence.UserRepository;
import com.Engineering.prova.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")// link URI di accesso
@RequiredArgsConstructor// il costruttore dev'essere inizializzato!
public class AuthController {

    private final UserRepository userRepository;// contiene i dati degli utenti nel DB
    private final PasswordEncoder passwordEncoder;// cifra la password tramite algoritmo di hash
    private final AuthenticationManager authenticationManager;// entità che autentica l'utente
    private final JwtService jwtService;

    @PostMapping("/register")// in questo caso, generiamo una chiamata POST per scrivere un utente all'interno del DB.
    public String register(@RequestBody AuthRequest request) {//la funzione riceve una richiesta di scrittura. Qualora l'utente esista, restituisce errore con il relativo messaggio
        if (userRepository.findByUsername(request.username).isPresent()) {
            return "Errore: username già esistente";
        }

        UserEntity user = new UserEntity();// creiamo un'entità user a cui affidare uno username ed una password
        user.setUsername(request.username);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setRole("ROLE_USER");// una volta salvata la passowrd e lo username, affidiamo il ruolo di user
        userRepository.save(user);// salviamo nel repository

        return "Utente registrato!";// messagio di esito positivo
    }

    @PostMapping("/login")// usiamo una richiesta POST per inserire i dati del login
    public String login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(// il sistema a questo punto genera un token per verificare la validità dell'utente
                new UsernamePasswordAuthenticationToken(request.username, request.password)// la funzione verifica che username e password inseriti siano corretti
        );

        return jwtService.generateToken(request.username);// qualora l'esito sia positivo, il sistema restituisce un token valido per la sessione
    }

    public static class AuthRequest { // è un contenitore usato da Springboot per dire come sarà fatto il file JSON contentenente lo username e la password
        public String username;
        public String password;
    }
}