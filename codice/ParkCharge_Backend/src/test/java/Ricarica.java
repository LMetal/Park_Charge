import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


public class Ricarica {
    GestoreRicariche gestoreRicariche = new GestoreRicariche();
    @Test
    public void richiediInterrompiRicarica(){
        var ricaricheList = gestoreRicariche.getRicariche();
        assertFalse(gestoreRicariche.getRicariche().stream().anyMatch(p -> p.getPrenotazione() == 100));
        assertEquals(10, ricaricheList.size());

        gestoreRicariche.addRicarica(20, 100);
        ricaricheList = gestoreRicariche.getRicariche();
        assertEquals(11, ricaricheList.size());
        assertTrue(ricaricheList.stream().anyMatch(p -> p.getPrenotazione() == 100));

        assertTrue(gestoreRicariche.stopRicaricaByPrenotazione("100"));
        ricaricheList = gestoreRicariche.getRicariche();
        assertEquals(10, ricaricheList.size());
        assertFalse(ricaricheList.stream().anyMatch(p -> p.getPrenotazione() == 100));



        ricaricheList = gestoreRicariche.getRicariche();
        assertFalse(gestoreRicariche.getRicariche().stream().anyMatch(p -> p.getPrenotazione() == 100));
        assertEquals(10, ricaricheList.size());

        gestoreRicariche.addRicarica(20, 100);
        gestoreRicariche.addRicarica(20, 101);
        ricaricheList = gestoreRicariche.getRicariche();
        assertEquals(12, ricaricheList.size());
        assertTrue(ricaricheList.stream().anyMatch(p -> p.getPrenotazione() == 100));
        assertTrue(ricaricheList.stream().anyMatch(p -> p.getPrenotazione() == 101));

        gestoreRicariche.stopRicaricaByPrenotazione("100");
        gestoreRicariche.stopRicaricaByPrenotazione("101");
        ricaricheList = gestoreRicariche.getRicariche();
        assertEquals(10, ricaricheList.size());
        assertFalse(ricaricheList.stream().anyMatch(p -> p.getPrenotazione() == 100));
        assertFalse(ricaricheList.stream().anyMatch(p -> p.getPrenotazione() == 101));
    }
}
