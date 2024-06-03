-- Pagamenti

CREATE TABLE "Pagamenti" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "tempo_arrivo" TEXT,
    "tempo_uscita" TEXT,
    "utente" TEXT,
    "posto" INTEGER,
    "ricarica" INTEGER,
    "costo" INTEGER,
    FOREIGN KEY ("costo") REFERENCES "PrezzoPosteggio"("id")
);

-- Costi

CREATE TABLE "Costi" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "costo_posteggio" FLOAT,
    "costo_ricarica" FLOAT,
    "penale" INTEGER,
    "costo_premium" INTEGER
);

-- Costo

CREATE TABLE "PrezzoPosteggio" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "costo_posteggio" FLOAT,
    "costo_ricarica" FLOAT,
    "penale" INTEGER
);