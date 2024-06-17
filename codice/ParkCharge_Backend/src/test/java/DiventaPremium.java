import DataBase.DbUtenti;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class DiventaPremium{
    DbUtenti dbUtenti = new DbUtenti();
    GestoreUtenti gestoreUtenti = new GestoreUtenti();

    @Test
    public void testDiventaPremium(){
        dbUtenti.update("INSERT INTO Credenziali (username,password) VALUES ('UsernameTest','PasswordTest')");
        dbUtenti.update("INSERT INTO Utente (username,nome,cognome,tipo,carta) VALUES ('UsernameTest','NomeTest','CognomeTest', 2 ,'CartaTest')");

        Utente utente = gestoreUtenti.getUtente("UsernameTest");
        assertEquals(utente.getTipo(),2);

        boolean update = gestoreUtenti.diventaPremium("UsernameTest");
        assertTrue(update);

        utente = gestoreUtenti.getUtente("UsernameTest");
        assertEquals(utente.getTipo(),1);

        dbUtenti.update("DELETE FROM Credenziali WHERE username = 'UsernameTest'");
        dbUtenti.update("DELETE FROM Utente WHERE username = 'UsernameTest'");
    }

    @Test
    public void testDiventaPremiumErrore(){
        boolean update = gestoreUtenti.diventaPremium("TestUtente");
        assertFalse(update);
    }
}
