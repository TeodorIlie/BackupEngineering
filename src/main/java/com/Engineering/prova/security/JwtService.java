/* Immaginiamo che un utente possa avviare una sessione. Questa sessione è salvata all'interno di un cookie.
 Un malintenzionato potrebbe intercettare i cookie e quindi la sessione dell'utente, rubando quindi di conseguenza tutti i dati che l'utente ha.
 Questo attacco è chiamato CSRF.
 Per evitare questi tipi di attacchi si usano dei token di tipo JWT.
 Il token ed il tempo di valenza è già stato stabilito nel file di proprietà del progetto.
 Come al solito, includiamo i package necessari, tra cui quelli per gestire i token JWT*/
package com.Engineering.prova.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service // Questa classe, fornisce il servizio per la gestione dei token JWT
public class JwtService {

    @Value("${jwt.secret}") // prende in ingresso il valore del JWT, utile per verificarne la validità
    private String secretKey;

    @Value("${jwt.expiration-ms}") // prende in ingresso il tempo in ms in cui il Token può essere usato. Superato quel termine, il token non può più essere usato
    private long expirationMs;

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey())
                .compact(); // permette di generare il token in base allo username, alla data di richiesta, al periodo di vita, alla chiave fornita dall'utente.
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    } // fornisce lo username dell'utente in base al token

    // verifica la validità del token in base al suo tempo di vita ed allo username a cui è associato
    public boolean isTokenValid(String token, String username) {
        Claims claims = getClaims(token);
        boolean notExpired = claims.getExpiration().after(new Date());
        return claims.getSubject().equals(username) && notExpired;
    }

    // fornisce in base al token il payload, ossia il contenuto del messaggio che l'utente vuole trasmettere/ricevere
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // fornisce la chiave usata per cifrare la comunicazione a partire dal token generato
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}