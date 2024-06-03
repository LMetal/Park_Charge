-- Utente

CREATE TABLE "Utente" (
    "username" TEXT PRIMARY KEY,
    "nome" TEXT,
    "cognome" TEXT,
    "tipo" INTEGER,
    "carta" TEXT,
    FOREIGN KEY ("carta") REFERENCES "Carta"("numero_carta")
);

-- Credenziali 

CREATE TABLE "Credenziali" (
    "username" TEXT PRIMARY KEY,
    "password" TEXT,
    FOREIGN KEY ("username") REFERENCES "Utente"("username")
);

-- Carta 

CREATE TABLE "Carta" (
    "nome" TEXT,
    "cognome" TEXT,
    "numero_carta" TEXT PRIMARY KEY,
    "scadenza" TEXT,
    "cvv" INTEGER
);