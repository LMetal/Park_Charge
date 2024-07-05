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

    public Ricariche getRicariche(int id) {
        var ricaricaUtente = dbRicariche.query("SELECT * FROM Ricarica WHERE id = \""+ id + "\"");
        if(ricaricaUtente == null) return null;
        if(ricaricaUtente.size() != 1) return null;

        return new Ricariche(ricaricaUtente.get(0));
    }

    public void addRicarica(int timeToCharge, int prenotazioneId){
        //add new charge
        //dbRicariche.update("");
    }
}
