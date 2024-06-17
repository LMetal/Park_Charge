import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class Login{

    GestoreUtenti gestoreUtenti = new GestoreUtenti();

    @Test
    public void testGetCredenziali(){
        Credenziali credenziali1 = gestoreUtenti.getCredenziali("mrossi","password123");
        assertEquals(credenziali1.getUsername(),"mrossi");
        assertEquals(credenziali1.getPassword(),"password123");

        Credenziali credenziali2 = gestoreUtenti.getCredenziali("paolorossi","password123");
        assertEquals(credenziali2,null);
    }

    @Test
    public void testGetUtente(){
        Utente utente1 = gestoreUtenti.getUtente("mrossi");
        assertEquals(utente1.getNome(),"Mario");
        assertEquals(utente1.getCognome(),"Rossi");
        assertEquals(utente1.getUsername(),"mrossi");
        assertEquals(utente1.getTipo(),1);
        assertEquals(utente1.getCarta(),"1234567812345678");

        Utente utente2 = gestoreUtenti.getUtente("paolorossi");
        assertEquals(utente2,null);
    }
}
