-- Questo script SQL consente di creare Utenti con un proprio nome, e un proprio ruolo.
-- Il ruolo definisce cosa può fare un Utente sul DB (leggere, scrivere, accedere ecc...)
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY, -- Codice per identificare l'utente, chiave primaria. Ogni utente deve averne uno, e due utenti diversi non possono avere lo stesso id
                       username VARCHAR(50) NOT NULL UNIQUE, -- Username dell'utente, anche in questo caso, unico, e deve contenere non più di 50 caratteri
                       password VARCHAR(255) NOT NULL, -- Password dell'utente, non può essere vuoto, massimo 255 caratteri
                       role VARCHAR(30) NOT NULL -- ruolo, massimo 30 caratteri, non può essere vuoto
);