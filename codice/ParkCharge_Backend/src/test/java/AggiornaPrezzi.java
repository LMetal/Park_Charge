import DataBase.DbStorico;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;
public class AggiornaPrezzi {

    GestorePagamenti gestorePagamenti = new GestorePagamenti();

    DbStorico dbStorico = new DbStorico();

    @Test
    public void testAggiornaPrezzi(){
        var prezziIniziali = gestorePagamenti.getCosti();

        Costi prezziAggiornati = new Costi(10,12,30,50);

        boolean update = gestorePagamenti.aggiornaPrezzi(prezziAggiornati);
        assertTrue(update);

        var costiAggiornati = gestorePagamenti.getCosti();
        assertEquals(costiAggiornati.get(0).get("costo_posteggio"),(double)10);
        assertEquals(costiAggiornati.get(0).get("costo_ricarica"),(double)12);
        assertEquals(costiAggiornati.get(0).get("penale"),(30));
        assertEquals(costiAggiornati.get(0).get("costo_premium"),50);

        dbStorico.update("UPDATE Costi SET costo_posteggio = \"" + prezziIniziali.get(0).get("costo_posteggio") + "\", costo_ricarica = \"" + prezziIniziali.get(0).get("costo_ricarica") + "\", penale = \"" + prezziIniziali.get(0).get("penale") + "\", costo_premium = \"" + prezziIniziali.get(0).get("costo_premium") + "\" WHERE id = '1'");
    }
}
