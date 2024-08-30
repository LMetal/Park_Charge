import DataBase.DbPrenotazioni;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class EliminaPrenotazione {

    DbPrenotazioni dbPrenotazioni = new DbPrenotazioni();

    GestorePosti gestorePosti = new GestorePosti();
    @Test
    public void testEliminaPrenotazione(){
        dbPrenotazioni.update("INSERT INTO Prenotazioni (id, tempo_arrivo, tempo_uscita, utente, posto) VALUES ('998','2024-06-12 08:00:00', '2024-06-12 09:00:00', 'utente1', 1)");

        assertTrue(gestorePosti.cancellaPrenotazione("998"));
        assertFalse(gestorePosti.cancellaPrenotazione("998"));
        assertNull(gestorePosti.getPrenotazione("998"));
    }
}
