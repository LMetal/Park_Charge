import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MonitoraRicariche {
    GestoreRicariche gestoreRicariche = new GestoreRicariche();

    @Test
    public void testGetRicariche(){
        ArrayList<Ricariche> ricariche = gestoreRicariche.getRicariche();

        assertEquals(10, ricariche.size());

        assertEquals(1, ricariche.get(0).getPrenotazione());
        assertEquals(50, ricariche.get(0).getKilowatt());
        assertEquals(90, ricariche.get(0).getDurata_ricarica());
        assertEquals(80, ricariche.get(0).getPercentuale_richiesta());
        assertEquals(1, ricariche.get(0).getMwbot());

        assertEquals(4, ricariche.get(3).getPrenotazione());
        assertEquals(55.5, ricariche.get(3).getKilowatt());
        assertEquals(105, ricariche.get(3).getDurata_ricarica());
        assertEquals(85, ricariche.get(3).getPercentuale_richiesta());
        assertEquals(1, ricariche.get(3).getMwbot());

        assertEquals(10, ricariche.get(9).getPrenotazione());
        assertEquals(50, ricariche.get(9).getKilowatt());
        assertEquals(85, ricariche.get(9).getDurata_ricarica());
        assertEquals(80, ricariche.get(9).getPercentuale_richiesta());
        assertEquals(1, ricariche.get(9).getMwbot());
    }
}
