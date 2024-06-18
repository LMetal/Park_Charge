import DataBase.DbPrenotazioni;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;
public class ModificaPrenotazione {

    GestorePosti gestorePosti = new GestorePosti();
    DbPrenotazioni dbPrenotazioni = new DbPrenotazioni();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Test
    public void testGetPrenotazione(){
        Prenotazioni prenotazione1 = gestorePosti.getPrenotazione("1");
        assertEquals(prenotazione1.getUtente(),"mrossi");
        assertEquals(prenotazione1.getPosto(),1);

        Prenotazioni prenotazione2 = gestorePosti.getPrenotazione("9999");
        assertEquals(prenotazione2,null);
    }

    @Test
    public void testModificaPrenotazione(){
        Prenotazioni vecchiaPrenotazione = new Prenotazioni();
        vecchiaPrenotazione.setId(998);
        vecchiaPrenotazione.setTempo_arrivo("2024-06-12 08:00:00");
        vecchiaPrenotazione.setTempo_uscita("2024-06-12 09:00:00");
        vecchiaPrenotazione.setUtente("utente1");

        dbPrenotazioni.update("INSERT INTO Prenotazioni (id, tempo_arrivo, tempo_uscita, utente, posto) VALUES ('998','2024-06-12 08:00:00', '2024-06-12 09:00:00', 'utente1', 1)");

        Prenotazioni nuovaPrenotazione = new Prenotazioni();
        nuovaPrenotazione.setTempo_arrivo("2024-06-12 09:00:00");
        nuovaPrenotazione.setTempo_uscita("2024-06-12 10:00:00");
        nuovaPrenotazione.setUtente("utente1");
        boolean update = gestorePosti.modificaPrenotazione(nuovaPrenotazione,vecchiaPrenotazione);
        assertTrue(update);

        dbPrenotazioni.update("DELETE FROM Prenotazioni WHERE utente = 'utente1'");
    }
}
