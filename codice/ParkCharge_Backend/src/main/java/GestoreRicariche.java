import DataBase.DbPrenotazioni;
import DataBase.DbRicariche;

import java.time.LocalDateTime;
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


        float kilowatt;
        int durata_ricarica;
        int percentuale_richiesta;
        int prenotazione;
        int mwbot;


        for(HashMap<String, Object> record : rs){
            kilowatt = Double.valueOf(record.get("kilowatt").toString()).floatValue();
            durata_ricarica = 10; //(int) record.get("durata_ricarica");
            percentuale_richiesta = (int) record.get("percentuale_richiesta");
            prenotazione = (int) record.get("prenotazione");
            mwbot = (int) record.get("MWBot");
            listaRicariche.add(new Ricariche(kilowatt, durata_ricarica, percentuale_richiesta, prenotazione, mwbot));
        }
        return listaRicariche;
    }
}
