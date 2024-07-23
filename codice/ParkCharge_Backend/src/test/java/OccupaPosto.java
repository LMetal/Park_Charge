import DataBase.DbPrenotazioni;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

public class OccupaPosto {
    DbPrenotazioni dbPrenotazioni = new DbPrenotazioni();

    GestorePosti gestorePosti = new GestorePosti();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Test
    public void testCreaPrenotazione(){
        Prenotazioni nuovaPrenotazione = new Prenotazioni();
        nuovaPrenotazione.setTempo_arrivo(LocalDateTime.now().format(formatter));
        nuovaPrenotazione.setTempo_uscita(LocalDateTime.now().plusHours(2).format(formatter));
        nuovaPrenotazione.setUtente("utente1");

        String risultato = String.valueOf(gestorePosti.creaPrenotazione(nuovaPrenotazione, 2, "occupa"));
        assertEquals("Successo", risultato);

        dbPrenotazioni.update("DELETE FROM Prenotazioni WHERE utente = 'utente1'");
    }

    @Test
    public void testCreaPrenotazioneErrore(){
        for (int i = 0; i < 11; i++){
            Prenotazioni nuovaPrenotazione = new Prenotazioni();
            nuovaPrenotazione.setTempo_arrivo(LocalDateTime.now().format(formatter));
            nuovaPrenotazione.setTempo_uscita(LocalDateTime.now().plusHours(2).format(formatter));
            nuovaPrenotazione.setUtente("utente1");

            String risultato = String.valueOf(gestorePosti.creaPrenotazione(nuovaPrenotazione, 2, "occupa"));
            if(i != 10) assertEquals("Successo", risultato);
            else assertEquals("Nessun posto disponibile nel periodo richiesto", risultato);
        }

        for (int i = 0; i < 10; i++){
            dbPrenotazioni.update("DELETE FROM Prenotazioni WHERE utente = 'utente1'");
        }
    }
}
