import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.Assert.*;

public class TestEDF {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void testAccettable(){
        Prenotazioni p1 = new Prenotazioni(0, "2024-06-11 11:00:00", "2024-06-11 12:00:00", "a", 1, false);
        Ricariche r1 = new Ricariche(30, 30, 30, 0, 1);
        Prenotazioni p2 = new Prenotazioni(1, "2024-06-11 11:00:00", "2024-06-11 12:30:00", "b", 2, false);
        Ricariche r2 = new Ricariche(30, 30, 30, 1, 1);
        Prenotazioni p3 = new Prenotazioni(2, "2024-06-11 12:00:00", "2024-06-11 12:50:00", "c", 3, false);
        Ricariche r3 = new Ricariche(40, 40, 40, 2, 1);
        Prenotazioni p4 = new Prenotazioni(3, "2024-06-11 12:30:00", "2024-06-11 15:00:00", "mrossi", 4, false);
        Ricariche r4 = new Ricariche(40, 40, 40, 3, 1);

        var listPrenotazioni = new ArrayList<Prenotazioni>();
        listPrenotazioni.add(p1);
        listPrenotazioni.add(p2);
        listPrenotazioni.add(p3);
        listPrenotazioni.add(p4);

        var listRicariche = new ArrayList<Ricariche>();
        //coda ricariche vuota, tempo ricarica < tempo sosta
        assertTrue(EDF.isAcceptable("mrossi", 20, listPrenotazioni, listRicariche, LocalDateTime.parse("2024-06-11 12:30:00", formatter), true));
        //coda ricariche vuota, tempo ricarica > tempo sosta
        assertFalse(EDF.isAcceptable("b", 40, listPrenotazioni, listRicariche, LocalDateTime.parse("2024-06-11 12:30:00", formatter), true));

        //coda con una ricarica accettata
        var l1 = new ArrayList<Ricariche>();
        //meno priorità
        l1.add(r1);
        assertTrue(EDF.isAcceptable("b", 20, listPrenotazioni, l1, LocalDateTime.parse("2024-06-11 11:00:00", formatter), true));
        assertFalse(EDF.isAcceptable("b", 70, listPrenotazioni, l1, LocalDateTime.parse("2024-06-11 11:00:00", formatter), true));
        //più priorità
        var l2 = new ArrayList<Ricariche>();
        l2.add(r2);
        assertTrue(EDF.isAcceptable("a", 20, listPrenotazioni, l2, LocalDateTime.parse("2024-06-11 11:00:00", formatter), true));
        assertFalse(EDF.isAcceptable("a", 70, listPrenotazioni, l2, LocalDateTime.parse("2024-06-11 11:00:00", formatter), true));

        var l3 = new ArrayList<Ricariche>();
        l3.add(r1);
        l3.add(r3);
        l3.add(r4);
        assertTrue(EDF.isAcceptable("b", 10, listPrenotazioni, l3, LocalDateTime.parse("2024-06-11 11:00:00", formatter), true));
        assertTrue(EDF.isAcceptable("b", 35, listPrenotazioni, l3, LocalDateTime.parse("2024-06-11 11:00:00", formatter), true));
        assertFalse(EDF.isAcceptable("b", 45, listPrenotazioni, l3, LocalDateTime.parse("2024-06-11 11:00:00", formatter), true));
    }

    @Test
    public void testScheduling(){
        Prenotazioni p1 = new Prenotazioni(0, "2024-06-11 11:00:00", "2024-06-11 12:00:00", "a", 1, false);
        Ricariche r1 = new Ricariche(30, 30, 30, 0, 1);
        Prenotazioni p2 = new Prenotazioni(1, "2024-06-11 11:00:00", "2024-06-11 12:30:00", "b", 2, false);
        Ricariche r2 = new Ricariche(30, 30, 30, 1, 1);
        Prenotazioni p3 = new Prenotazioni(2, "2024-06-11 12:00:00", "2024-06-11 12:20:00", "c", 3, false);
        Ricariche r3 = new Ricariche(10, 10, 10, 2, 1);
        Prenotazioni p4 = new Prenotazioni(3, "2024-06-11 12:30:00", "2024-06-11 15:00:00", "mrossi", 4, false);
        Ricariche r4 = new Ricariche(40, 40, 40, 3, 1);

        ArrayList<Prenotazioni> prenotazioniList = new ArrayList<>();
        ArrayList<Prenotazioni> emptyPrenotazioniList = new ArrayList<>();
        prenotazioniList.add(p1);
        prenotazioniList.add(p2);
        prenotazioniList.add(p3);
        prenotazioniList.add(p4);

        ArrayList<Ricariche> emptyRicaricheList = new ArrayList<>();
        ArrayList<Ricariche> ricaricheList = new ArrayList<>();
        ricaricheList.add(r1);
        ricaricheList.add(r2);
        ricaricheList.add(r3);
        ricaricheList.add(r4);

        //nessuna prenotazione o ricarica
        assertNull(EDF.getJobPosto(emptyPrenotazioniList, emptyRicaricheList, true));
        //nessuna ricarica
        assertNull(EDF.getJobPosto(prenotazioniList, emptyRicaricheList, true));
        //nessuna prenotazione
        assertNull(EDF.getJobPosto(emptyPrenotazioniList, ricaricheList, true));

        // prenotazioni e ricariche
        Assertions.assertEquals(1, Objects.requireNonNull(EDF.getJobPosto(prenotazioniList, ricaricheList, true)).getPosto());
        //ricarica prenotazione 0 completata
        ricaricheList.remove(0);
        assertEquals(3, Objects.requireNonNull(EDF.getJobPosto(prenotazioniList, ricaricheList, true)).getPosto());


    }
}
