import DataBase.DbUtenti;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;
public class CreaUtente{

    DbUtenti dbUtenti = new DbUtenti();
    GestoreUtenti gestoreUtenti = new GestoreUtenti();

    @Test
    public void testCreaUtente(){
        Utente utente = new  Utente("NomeTest","CognomeTest", "UsernameTest",1,"CartaTest");
        Credenziali credenziali = new Credenziali("UsernameTest","PasswordTest");
        String created = gestoreUtenti.creaUtenti(utente,credenziali);

        assertEquals(created,"Successo");

        dbUtenti.update("DELETE FROM Credenziali WHERE username = 'UsernameTest'");
        dbUtenti.update("DELETE FROM Utente WHERE username = 'UsernameTest'");
    }

    @Test
    public void testCreaUtenteErrore(){
        Utente utente = new  Utente("NomeTest","CognomeTest", "mrossi",1,"CartaTest");
        Credenziali credenziali = new Credenziali("UsernameTest","PasswordTest");
        String created = gestoreUtenti.creaUtenti(utente,credenziali);

        assertEquals(created,"Username gia esistente");
    }
}