import DataBase.DbRicariche;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;



public class Ricarica {
    GestoreRicariche gestoreRicariche = new GestoreRicariche();
    DbRicariche dbRicariche = new DbRicariche();
    @Test
    public void richiediInterrompiRicarica(){
        gestoreRicariche.addRicarica(20, 1000, true);
        ArrayList<Ricariche> ricaricheList = gestoreRicariche.getRicaricheByPrenotazione("1000");

        assertEquals(1, ricaricheList.size());
        assertEquals(20, ricaricheList.get(0).getPercentuale_richiesta());
        assertEquals(1000, ricaricheList.get(0).getPrenotazione());
        assertEquals(0, ricaricheList.get(0).getPercentuale_erogata());
        assertEquals(1, ricaricheList.get(0).getMwbot());

        assertTrue(gestoreRicariche.stopRicaricaByPrenotazione("1000", true));

        ricaricheList = gestoreRicariche.getRicaricheByPrenotazione("1000");
        assertEquals(1, ricaricheList.size());
        assertEquals(0, ricaricheList.get(0).getPercentuale_richiesta());
        assertEquals(1000, ricaricheList.get(0).getPrenotazione());
        assertEquals(0, ricaricheList.get(0).getPercentuale_erogata());
        assertEquals(1, ricaricheList.get(0).getMwbot());

        //simulo ricarica in corso in parte erogata
        dbRicariche.update("INSERT INTO Ricarica(prenotazione, percentuale_richiesta, percentuale_erogata, MWBot) VALUES ('1000','50', '11', '1');");

        ricaricheList = gestoreRicariche.getRicaricheByPrenotazione("1000");
        assertEquals(2, ricaricheList.size());

        assertEquals(0, ricaricheList.get(0).getPercentuale_richiesta());
        assertEquals(1000, ricaricheList.get(0).getPrenotazione());
        assertEquals(0, ricaricheList.get(0).getPercentuale_erogata());
        assertEquals(1, ricaricheList.get(0).getMwbot());

        assertEquals(50, ricaricheList.get(1).getPercentuale_richiesta());
        assertEquals(1000, ricaricheList.get(1).getPrenotazione());
        assertEquals(11, ricaricheList.get(1).getPercentuale_erogata());
        assertEquals(1, ricaricheList.get(1).getMwbot());

        dbRicariche.update("DELETE FROM Ricarica WHERE prenotazione = 1000");

        //due ricariche per prenotazioni diverse
        gestoreRicariche.addRicarica(25, 1000, true);
        gestoreRicariche.addRicarica(40, 1001, true);

        assertEquals(1, gestoreRicariche.getRicaricheByPrenotazione("1000").size());
        assertEquals(1, gestoreRicariche.getRicaricheByPrenotazione("1001").size());

        dbRicariche.update("DELETE FROM Ricarica WHERE prenotazione = 1000");
        dbRicariche.update("DELETE FROM Ricarica WHERE prenotazione = 1001");

    }
}
