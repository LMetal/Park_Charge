import DataBase.DbPrenotazioni;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class GestorePosti {
    private DbPrenotazioni dbPrenotazioni;
    DateTimeFormatter formatter;
    public GestorePosti(){
        this.dbPrenotazioni = new DbPrenotazioni();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public ArrayList<Prenotazioni> getPrenotazioni(){
        ArrayList<Prenotazioni> listaPrenotazioni = new ArrayList<>();
        String comandoSql = "SELECT * FROM Prenotazioni";
        System.out.println(comandoSql);
        var rs = dbPrenotazioni.query(comandoSql);

        for(HashMap<String, Object> record : rs){
            listaPrenotazioni.add(new Prenotazioni((Integer) record.get("id"), LocalDateTime.parse((CharSequence) record.get("tempo_arrivo"), formatter), LocalDateTime.parse((CharSequence) record.get("tempo_uscita"), formatter), (String) record.get("utente"), (Integer) record.get("posto")));
        }
        return listaPrenotazioni;
    }
}
