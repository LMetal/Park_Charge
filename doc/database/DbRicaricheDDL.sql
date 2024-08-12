-- Ricarica

CREATE TABLE "Ricarica" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "prenotazione" INTEGER,
    "percentuale_richiesta" INTEGER,
    "percentuale_erogata" INTEGER,
    "MWBot" INTEGER,
    FOREIGN KEY ("MWBot") REFERENCES "MWBot"("id")
);

-- MWBot

CREATE TABLE "MWBot" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "stato" TEXT
);