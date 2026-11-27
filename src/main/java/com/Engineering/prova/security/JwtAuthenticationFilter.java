/*Questa classe ha lo scopo di verificare la validità dello username e del token fornito*/
package com.Engineering.prova.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
//creiamo un entità che elabori lo JWT token e lo username dell'utente. Eredita le caratterische della classe madre OncePerFilter
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;// fornisce i servizi per autenticare il token JWT
    private final UserDetailsService userDetailsService;// fornisce i servizi per autenticare l'utente associato al token

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override// funzione filtro, sovrascrive la funzione della classe madre
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // il programma avvia la pratica specificata dall'header, che in questo caso coincide con l'autorizzazione.
        String header = request.getHeader("Authorization");
        // il sistema verifica che l'header non sia vuouto, e che inizi con la stringa "Bearer". Comincia a leggere dal carattere 7 e poi restituisce il token
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String username = jwtService.extractUsername(token);
            // verifica che il campo utente non sia vuoto e che non sia già stato validato nella sessione corrente
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsService.loadUserByUsername(username);
                // verifica che il token JWT sia dell'utente e che questo risulti valido
                if (jwtService.isTokenValid(token, user.getUsername())) {
                    // fornisce l'autorizzazione all'utente di accedere al sistema
                    var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
// fornisce l'esito della chiamata dell'utente
        filterChain.doFilter(request, response);
    }
}