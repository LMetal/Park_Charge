-- Posto auto

CREATE TABLE "PostoAuto" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "disponibilita" BOOLEAN
);

-- Prenotazioni

CREATE TABLE "Prenotazioni" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "tempo_arrivo" TEXT,
    "tempo_uscita" TEXT,
    "utente" TEXT,
    "posto" INTEGER
);