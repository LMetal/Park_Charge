-- Utente

CREATE TABLE "Utente" (
    "username" TEXT PRIMARY KEY,
    "nome" TEXT,
    "cognome" TEXT,
    "tipo" INTEGER,
    "carta" TEXT,
);

-- Credenziali 

CREATE TABLE "Credenziali" (
    "username" TEXT PRIMARY KEY,
    "password" TEXT,
    FOREIGN KEY ("username") REFERENCES "Utente"("username")
);