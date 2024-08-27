import DataBase.DbPrenotazioni;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MonitoraPrenotazioni {
    GestorePosti gestorePosti = new GestorePosti();
    DbPrenotazioni dbPrenotazioni = new DbPrenotazioni();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void testMonitoraPrenotazioni() {
        // Inserisci i dati necessari nel database
        dbPrenotazioni.update("INSERT INTO Prenotazioni (id, tempo_arrivo, tempo_uscita, utente, posto, penale) VALUES " +
                "('1', '2024-06-01 08:00:00', '2024-06-01 10:00:00', 'mrossi', 1, '0')," +
                "('2', '2024-06-01 09:00:00', '2024-06-01 11:00:00', 'lverdi', 2, '0')," +
                "('3', '2024-06-01 08:30:00', '2024-06-01 09:30:00', 'gbianchi', 3, '0')," +
                "('4', '2024-06-01 10:00:00', '2024-06-01 12:00:00', 'aneri', 4, '0')," +
                "('5', '2024-06-01 07:00:00', '2024-06-01 08:00:00', 'pgialli', 5, '0')," +
                "('6', '2024-06-01 09:30:00', '2024-06-01 10:30:00', 'cblu', 6, '0')," +
                "('7', '2024-06-01 11:00:00', '2024-06-01 12:30:00', 'lrossi', 7, '0')," +
                "('8', '2024-06-01 08:45:00', '2024-06-01 10:15:00', 'fverdi', 8, '0')," +
                "('9', '2024-06-01 10:30:00', '2024-06-01 11:30:00', 'abianchi', 9, '0')," +
                "('10', '2024-06-01 07:30:00', '2024-06-01 09:00:00', 'eneri', 10, '0')");

        // Recupera le prenotazioni dal gestore
        ArrayList<Prenotazioni> prenotazioni = gestorePosti.getPrenotazioni();

        assertEquals(1, prenotazioni.get(0).getId());
        assertEquals(LocalDateTime.parse("2024-06-01 08:00:00", formatter), prenotazioni.get(0).getTempo_arrivo());
        assertEquals(LocalDateTime.parse("2024-06-01 10:00:00", formatter), prenotazioni.get(0).getTempo_uscita());
        assertEquals("mrossi", prenotazioni.get(0).getUtente());
        assertEquals(1, prenotazioni.get(0).getPosto());

        assertEquals(2, prenotazioni.get(1).getId());
        assertEquals(LocalDateTime.parse("2024-06-01 09:00:00", formatter), prenotazioni.get(1).getTempo_arrivo());
        assertEquals(LocalDateTime.parse("2024-06-01 11:00:00", formatter), prenotazioni.get(1).getTempo_uscita());
        assertEquals("lverdi", prenotazioni.get(1).getUtente());
        assertEquals(2, prenotazioni.get(1).getPosto());

        assertEquals(10, prenotazioni.get(9).getId());
        assertEquals(LocalDateTime.parse("2024-06-01 07:30:00", formatter), prenotazioni.get(9).getTempo_arrivo());
        assertEquals(LocalDateTime.parse("2024-06-01 09:00:00", formatter), prenotazioni.get(9).getTempo_uscita());
        assertEquals("eneri", prenotazioni.get(9).getUtente());
        assertEquals(10, prenotazioni.get(9).getPosto());

        // Cancella le prenotazioni dopo il test
        dbPrenotazioni.update("DELETE FROM Prenotazioni WHERE id BETWEEN 1 AND 10");
    }


    /* database
    ('2024-06-01 08:00:00', '2024-06-01 10:00:00', 'mrossi', 1),
    ('2024-06-01 09:00:00', '2024-06-01 11:00:00', 'lverdi', 2),
    ('2024-06-01 08:30:00', '2024-06-01 09:30:00', 'gbianchi', 3),
    ('2024-06-01 10:00:00', '2024-06-01 12:00:00', 'aneri', 4),
    ('2024-06-01 07:00:00', '2024-06-01 08:00:00', 'pgialli', 5),
    ('2024-06-01 09:30:00', '2024-06-01 10:30:00', 'cblu', 6),
    ('2024-06-01 11:00:00', '2024-06-01 12:30:00', 'lrossi', 7),
    ('2024-06-01 08:45:00', '2024-06-01 10:15:00', 'fverdi', 8),
    ('2024-06-01 10:30:00', '2024-06-01 11:30:00', 'abianchi', 9),
    ('2024-06-01 07:30:00', '2024-06-01 09:00:00', 'eneri', 10);
     */

}
