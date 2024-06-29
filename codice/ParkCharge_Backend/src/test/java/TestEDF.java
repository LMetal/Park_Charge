import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestEDF {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void testAccettable(){
        Prenotazioni p1 = new Prenotazioni(0, "2024-06-11 11:00:00", "2024-06-11 12:00:00", "a", 1);
        Ricariche r1 = new Ricariche(30, 30, 30, 0, 1);
        Prenotazioni p2 = new Prenotazioni(1, "2024-06-11 11:00:00", "2024-06-11 12:30:00", "b", 2);
        Ricariche r2 = new Ricariche(30, 30, 30, 1, 1);
        Prenotazioni p3 = new Prenotazioni(2, "2024-06-11 12:00:00", "2024-06-11 12:50:00", "c", 3);
        Ricariche r3 = new Ricariche(40, 40, 40, 2, 1);
        Prenotazioni p4 = new Prenotazioni(3, "2024-06-11 12:30:00", "2024-06-11 15:00:00", "mrossi", 4);
        Ricariche r4 = new Ricariche(40, 40, 40, 3, 1);

        var listPrenotazioni = new ArrayList<Prenotazioni>();
        listPrenotazioni.add(p1);
        listPrenotazioni.add(p2);
        listPrenotazioni.add(p3);
        listPrenotazioni.add(p4);

        var listRicariche = new ArrayList<Ricariche>();
        //coda ricariche vuota, tempo ricarica < tempo sosta
        assertTrue(EDF.isAccettable("mrossi", 20, listPrenotazioni, listRicariche, LocalDateTime.parse("2024-06-11 12:30:00", formatter)));
        //coda ricariche vuota, tempo ricarica > tempo sosta
        assertFalse(EDF.isAccettable("b", 40, listPrenotazioni, listRicariche, LocalDateTime.parse("2024-06-11 12:30:00", formatter)));

        //coda con una ricarica accettata
        var l1 = new ArrayList<Ricariche>();
        //meno priorità
        l1.add(r1);
        assertTrue(EDF.isAccettable("b", 20, listPrenotazioni, l1, LocalDateTime.parse("2024-06-11 11:00:00", formatter)));
        assertFalse(EDF.isAccettable("b", 70, listPrenotazioni, l1, LocalDateTime.parse("2024-06-11 11:00:00", formatter)));
        //più priorità
        var l2 = new ArrayList<Ricariche>();
        l2.add(r2);
        assertTrue(EDF.isAccettable("a", 20, listPrenotazioni, l2, LocalDateTime.parse("2024-06-11 11:00:00", formatter)));
        assertFalse(EDF.isAccettable("a", 70, listPrenotazioni, l2, LocalDateTime.parse("2024-06-11 11:00:00", formatter)));

        var l3 = new ArrayList<Ricariche>();
        l3.add(r1);
        l3.add(r3);
        l3.add(r4);
        assertTrue(EDF.isAccettable("b", 10, listPrenotazioni, l3, LocalDateTime.parse("2024-06-11 11:00:00", formatter)));
        assertTrue(EDF.isAccettable("b", 35, listPrenotazioni, l3, LocalDateTime.parse("2024-06-11 11:00:00", formatter)));
        assertFalse(EDF.isAccettable("b", 45, listPrenotazioni, l3, LocalDateTime.parse("2024-06-11 11:00:00", formatter)));
    }
}
