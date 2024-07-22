import DataBase.DbUtenti;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class ModificaDati{

    GestoreUtenti gestoreUtenti = new GestoreUtenti();
    DbUtenti dbUtenti = new DbUtenti();

    @Test
    public void modificaDati(){
        dbUtenti.update("INSERT INTO Credenziali (username,password) VALUES ('UsernameTest','PasswordTest')");
        dbUtenti.update("INSERT INTO Utente (username,nome,cognome,tipo,carta) VALUES ('UsernameTest','NomeTest','CognomeTest', 1 ,'CartaTest')");

        Utente utenteModifiche = new Utente("TestNome","TestCognome",null,0,"TestCarta");
        boolean update = gestoreUtenti.modificaDatiUtente("UsernameTest",utenteModifiche);
        assertTrue(update);

        Utente utente = gestoreUtenti.getUtente("UsernameTest");
        assertEquals(utente.getNome(),"TestNome");
        assertEquals(utente.getCognome(),"TestCognome");
        assertEquals(utente.getCarta(),"TestCarta");

        dbUtenti.update("DELETE FROM Credenziali WHERE username = 'UsernameTest'");
        dbUtenti.update("DELETE FROM Utente WHERE username = 'UsernameTest'");
    }

    @Test
    public void modificaDatiErrore(){
        dbUtenti.update("INSERT INTO Credenziali (username,password) VALUES ('UsernameTest','PasswordTest')");
        dbUtenti.update("INSERT INTO Utente (username,nome,cognome,tipo,carta) VALUES ('UsernameTest','NomeTest','CognomeTest', 1 ,'CartaTest')");

        Utente utenteModifiche = new Utente("TestNome","TestCognome",null,0,"TestCarta");
        boolean update = gestoreUtenti.modificaDatiUtente("TestUsername",utenteModifiche);
        assertFalse(update);
        dbUtenti.update("DELETE FROM Credenziali WHERE username = 'UsernameTest'");
        dbUtenti.update("DELETE FROM Utente WHERE username = 'UsernameTest'");
    }
}