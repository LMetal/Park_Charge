import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MonitoraPosti {
    GestorePosti gestorePosti = new GestorePosti();

    @Test
    public void getStatoPosti(){
        ArrayList<HashMap<String, Object>> statoPosti = gestorePosti.getStatoPosti();

        assertEquals(10, statoPosti.size());

        assertEquals(1, statoPosti.get(0).get("id"));
        assertEquals(0, statoPosti.get(0).get("disponibilita"));
        assertEquals(2, statoPosti.get(1).get("id"));
        assertEquals(0, statoPosti.get(1).get("disponibilita"));
        assertEquals(3, statoPosti.get(2).get("id"));
        assertEquals(0, statoPosti.get(2).get("disponibilita"));
        assertEquals(9, statoPosti.get(8).get("id"));
        assertEquals(0, statoPosti.get(8).get ("disponibilita"));
        assertEquals(10, statoPosti.get(9).get("id"));
        assertEquals(0, statoPosti.get(9).get("disponibilita"));

    }
}
