-- Consente a taskuser di usare lo schema public e creare oggetti al suo interno
GRANT USAGE, CREATE ON SCHEMA public TO taskuser;

-- Assegna a taskuser i permessi di lettura e scrittura su tutte le tabelle esistenti nello schema public
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO taskuser;

-- Assegna a taskuser i permessi sulle sequenze esistenti (necessarie ad esempio per chiavi primarie serial/identity)
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO taskuser;

-- Applica automaticamente i permessi sulle tabelle create in futuro nello schema public
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO taskuser;

-- Applica automaticamente i permessi sulle sequenze create in futuro nello schema public
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO taskuser;