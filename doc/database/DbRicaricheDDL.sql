-- Ricarica

CREATE TABLE "Ricarica" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "prenotazione" INTEGER,
    "kilowatt" FLOAT,
    "durata_ricarica" TEXT,
    "percentuale_richiesta" INTEGER,
    "MWBot" INTEGER,
    FOREIGN KEY ("MWBot") REFERENCES "MWBot"("id")
);

-- MWBot

CREATE TABLE "MWBot" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "stato" TEXT
);