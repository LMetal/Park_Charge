import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MonitoraRicariche {
    GestoreRicariche gestoreRicariche = new GestoreRicariche();

    @Test
    public void testGetRicariche(){
        ArrayList<Ricariche> ricariche = gestoreRicariche.getRicariche();

        assertEquals(1, ricariche.get(0).getPrenotazione());
        assertEquals(80, ricariche.get(0).getPercentuale_richiesta());
        assertEquals(0, ricariche.get(0).getPercentuale_erogata());
        assertEquals(1, ricariche.get(0).getMwbot());

        assertEquals(4, ricariche.get(3).getPrenotazione());
        assertEquals(80, ricariche.get(0).getPercentuale_richiesta());
        assertEquals(0, ricariche.get(0).getPercentuale_erogata());
        assertEquals(1, ricariche.get(3).getMwbot());

        assertEquals(10, ricariche.get(9).getPrenotazione());
        assertEquals(80, ricariche.get(0).getPercentuale_richiesta());
        assertEquals(0, ricariche.get(0).getPercentuale_erogata());
        assertEquals(1, ricariche.get(9).getMwbot());
    }
}
