import DataBase.DbPrenotazioni;
import DataBase.DbRicariche;

import java.util.ArrayList;
import java.util.HashMap;

public class GestoreRicariche {
    private DbRicariche dbRicariche;
    private DbPrenotazioni dbPrenotazioni;

    public GestoreRicariche(){
        this.dbPrenotazioni = new DbPrenotazioni();
        this.dbRicariche = new DbRicariche();
    }

    public ArrayList<Ricariche> getRicariche() {
        ArrayList<Ricariche> listaRicariche = new ArrayList<>();
        String comandoSql = "SELECT * FROM Ricarica";
        var rs = dbRicariche.query(comandoSql);

        for(HashMap<String, Object> record : rs){
            listaRicariche.add(new Ricariche(record));
        }
        return listaRicariche;
    }

    public Ricariche getRicariche(String id) {
        var ricaricaUtente = dbRicariche.query("SELECT * FROM Ricarica WHERE id = \""+ id + "\"");
        if(ricaricaUtente == null) return null;
        if(ricaricaUtente.size() != 1) return null;

        return new Ricariche(ricaricaUtente.get(0));
    }

    public void addRicarica(int timeToCharge, int prenotazioneId){
        //add new charge
        dbRicariche.update("INSERT INTO Ricarica(prenotazione, kilowatt, durata_ricarica, percentuale_richiesta, MWBot) VALUES ("+prenotazioneId+", "+timeToCharge+", "+timeToCharge+", "+timeToCharge+", '1');");
    }

    public boolean stopRicaricaByPrenotazione(String id_prenotazione) {
        try{
            dbRicariche.update("DELETE FROM Ricarica WHERE prenotazione = \"" + id_prenotazione + "\";");
        } catch (Exception e){
            return false;
        }
        return true;
    }


    public ArrayList<Ricariche> getRicaricheByPrenotazione(String id_prenotazione) {
        var ricaricaUtente = dbRicariche.query("SELECT * FROM Ricarica WHERE prenotazione = \""+ id_prenotazione + "\"");

        if(ricaricaUtente == null || ricaricaUtente.isEmpty()) return null;

        ArrayList<Ricariche> ricaricheList = new ArrayList<>();
        for (var ricarica : ricaricaUtente) {
            ricaricheList.add(new Ricariche(ricarica));
        }

        return ricaricheList;
    }
}
