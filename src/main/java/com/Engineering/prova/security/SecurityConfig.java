/* Una volta stabilito che il token sia valido, dobbiamo validare l'utente attraverso lo username e la password.
 Security config stabilisce le impostazioni con cui la validazione dev'essere effettuata*/
package com.Engineering.prova.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean// Consente di criptare la password tramite un algoritmo di Hash (es. SHA-256)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();// Restituisce un oggetto capace di criptare la password
    }

    @Bean// controllore del login: prendendo in ingresso lo username e la password, ci dice se quello che abbiamo inserito risulta nel DB
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean//controlla che l'utente sia valido e autenticato, e che possa accedere alle risorse della pagina
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);//disattiva la protezione CSRF (cross site request forgery). Serve in quanto, se provassimo ad usare POSTMAN o altri servizi simili, il sistema ccontinuerebbe a restituire errore continuamente
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));//disattiva le sessioni stateful (JSESSIONID) perché usiamo JWT
        http.authorizeHttpRequests(auth -> { //definisce le regole con cui posso accedere tramite URI o URL
            auth.requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll();// se cerco di accedere alla pagina di login, il sistema mi consente di farlo
            auth.anyRequest().authenticated();//se provo ad accedere ad una risorsa della pagina, devo per forza autenticarmi
        });

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);//controlla se l'utente sia già stato autenticato. In quel caso, non è neccessario ripetere la procedura di autenticazione da zero
        return http.build();
    }
}