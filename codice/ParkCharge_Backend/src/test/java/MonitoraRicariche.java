import DataBase.DbRicariche;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class MonitoraRicariche {
    GestoreRicariche gestoreRicariche = new GestoreRicariche();

    DbRicariche dbRicariche = new DbRicariche();

    @Test
    public void testGetRicariche(){
        gestoreRicariche.addRicarica(10, 1000, true);
        gestoreRicariche.addRicarica(25, 1001, true);
        dbRicariche.update("INSERT INTO Ricarica(prenotazione, percentuale_richiesta, percentuale_erogata, MWBot) VALUES ('1002','50', '11', '1');");

        ArrayList<Ricariche> ricariche = gestoreRicariche.getRicariche();

        Ricariche r1000 = ricariche.stream().filter(r->r.getPrenotazione() == 1000).findFirst().orElse(null);
        Ricariche r1001 = ricariche.stream().filter(r->r.getPrenotazione() == 1001).findFirst().orElse(null);
        Ricariche r1002 = ricariche.stream().filter(r->r.getPrenotazione() == 1002).findFirst().orElse(null);

        assertNotNull(r1000);
        assertEquals(1000, r1000.getPrenotazione());
        assertEquals(10, r1000.getPercentuale_richiesta());
        assertEquals(0, r1000.getPercentuale_erogata());

        assertNotNull(r1001);
        assertEquals(1001, r1001.getPrenotazione());
        assertEquals(25, r1001.getPercentuale_richiesta());
        assertEquals(0, r1001.getPercentuale_erogata());

        assertNotNull(r1002);
        assertEquals(1002, r1002.getPrenotazione());
        assertEquals(50, r1002.getPercentuale_richiesta());
        assertEquals(11, r1002.getPercentuale_erogata());


        dbRicariche.update("DELETE FROM Ricarica WHERE prenotazione = '1000'");
        dbRicariche.update("DELETE FROM Ricarica WHERE prenotazione = '1001'");
        dbRicariche.update("DELETE FROM Ricarica WHERE prenotazione = '1002'");
        dbRicariche.update("UPDATE MWBot SET idPrenotazione = \"-1\", stato = \"Finito\" WHERE id = 1");
    }
}
