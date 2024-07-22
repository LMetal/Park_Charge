import DataBase.DbUtenti;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class DiventaPremium{
    DbUtenti dbUtenti = new DbUtenti();
    GestoreUtenti gestoreUtenti = new GestoreUtenti();

    @Test
    public void testDiventaPremium(){
        dbUtenti.update("INSERT INTO Credenziali (username,password) VALUES ('UsernameTestPremium','PasswordTest')");
        dbUtenti.update("INSERT INTO Utente (username,nome,cognome,tipo,carta) VALUES ('UsernameTestPremium','NomeTest','CognomeTest', 2 ,'CartaTest')");

        Utente utente = gestoreUtenti.getUtente("UsernameTestPremium");
        assertEquals(utente.getTipo(),2);

        boolean update = gestoreUtenti.diventaPremium("UsernameTestPremium");
        assertTrue(update);

        utente = gestoreUtenti.getUtente("UsernameTestPremium");
        assertEquals(utente.getTipo(),1);

        dbUtenti.update("DELETE FROM Credenziali WHERE username = 'UsernameTestPremium'");
        dbUtenti.update("DELETE FROM Utente WHERE username = 'UsernameTestPremium'");
    }

    @Test
    public void testDiventaPremiumErrore(){
        boolean update = gestoreUtenti.diventaPremium("TestUtente");
        assertFalse(update);
    }
}
