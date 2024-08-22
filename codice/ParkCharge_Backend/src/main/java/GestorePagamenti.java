import DataBase.DbPrenotazioni;
import DataBase.DbStorico;
import DataBase.DbUtenti;
import com.google.gson.Gson;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorePagamenti {
    private DbStorico dbStorico;
    private DbPrenotazioni dbPrenotazioni;
    private Backend backend;

    // Costruttore
    public GestorePagamenti(){
        this.dbStorico = new DbStorico();
        this.backend = new Backend();
        this.dbPrenotazioni = new DbPrenotazioni();
    }

    // Metodo per ottenere tutti i costi dal database
    public ArrayList<HashMap<String, Object>> getCosti(){
        String comandoSql = "SELECT * FROM Costi";
        System.out.println(comandoSql);

        // Esegue la query per ottenere i costi dal database storico
        var rs = dbStorico.query(comandoSql);
        return rs;
    }

    // Metodo per ottenere il costo del servizio premium dal database
    public int getCostoPremium() {
        String comandoSql = "SELECT costo_premium FROM Costi";
        System.out.println(comandoSql);

        // Esegue la query per ottenere il costo del servizio premium dal database storico
        ArrayList<HashMap<String, Object>> rs = dbStorico.query(comandoSql);

        // Restituisce il costo premium come intero
        return (Integer) rs.get(0).get("costo_premium");
    }

    // Metodo per aggiornare i prezzi nel database
    public boolean aggiornaPrezzi(Costi costo) {
        // Costruisce la query di aggiornamento dei prezzi con i dati forniti dall'oggetto Costi
        String comandoSql = "UPDATE Costi SET costo_posteggio = \"" + costo.getCosto_posteggio() + "\", costo_ricarica = \"" + costo.getCosto_ricarica() + "\", penale = \"" + costo.getPenale() + "\", costo_premium = \"" + costo.getCosto_premium() + "\" WHERE id = \"" + 1 + "\";";
        System.out.println(comandoSql);

        // Esegue la query di aggiornamento nel database storico e restituisce il risultato
        return dbStorico.update(comandoSql);
    }

    public ArrayList<HashMap<String, Object>> getStorico() {
        String comandoSql = "SELECT * FROM Pagamenti\n" +
                "JOIN PrezzoPosteggio \n" +
                "WHERE Pagamenti.costo = PrezzoPosteggio.id";

        return dbStorico.query(comandoSql);
    }

    public void effettuaPagamento(Prenotazioni prenotazioneConclusa, Ricariche ricaricaConclusa) {
        HashMap<String, Object> costiAttuali = this.getCosti().get(0);
        HashMap<String,Object> prezzoPosteggio = new HashMap<>();

        Duration tempoSosta = Duration.between(prenotazioneConclusa.getTempo_arrivo(), prenotazioneConclusa.getTempo_uscita());
        Object costoPosteggioObj = costiAttuali.get("costo_posteggio");

        float costoPosteggio = ((Number) costoPosteggioObj).floatValue();
        float minutiSosta = (float) tempoSosta.toMinutes();
        float costoSosta = minutiSosta * costoPosteggio;

        float costoRicarica = 0;
        int penale = 0;
        int ricarica = 0;

        if(prenotazioneConclusa.getPenale()){
            penale = (int) costiAttuali.get("penale");
            costoSosta += penale;
        }
        prezzoPosteggio.put("tempoSosta", tempoSosta.toMinutes());
        prezzoPosteggio.put("costoSosta", costoSosta);
        if( ricaricaConclusa == null){
            prezzoPosteggio.put("kilowattUsati",0);
            prezzoPosteggio.put("costoRicarica",0);
        }
        else{
            ricarica = ricaricaConclusa.getPrenotazione();
            int kilowattUsati = ricaricaConclusa.getPercentuale_erogata(); // 1% = 1 KW
            costoRicarica = kilowattUsati * (float) costiAttuali.get("costo_ricarica");
            prezzoPosteggio.put("kilowattUsati", kilowattUsati);
            prezzoPosteggio.put("costoRicarica", costoRicarica);
        }
        System.out.println("siamo arrivati qua");
        Gson gson = new Gson();
        backend.publish("ParkCharge/Notifiche/SostaConclusa/" + prenotazioneConclusa.getUtente(), gson.toJson(prezzoPosteggio));
        int lastId;

        String deletConclusa = "DELETE FROM Prenotazioni WHERE id = \"" + prenotazioneConclusa.getId() + "\";";
        dbPrenotazioni.update(deletConclusa);

        String insertPrezzoPosteggio = "INSERT INTO PrezzoPosteggio (costo_posteggio,costo_ricarica,penale) VALUES ('" + costoSosta + "','" + costoRicarica + "','" + penale + "');";
        dbStorico.update(insertPrezzoPosteggio);

        String selectLastId = "SELECT last_insert_rowid()";
        var rs = dbStorico.query(selectLastId);
        lastId =(int) rs.get(0).get("last_insert_rowid()");

        String insertPagamento = "INSERT INTO Pagamenti (id,tempo_arrivo,tempo_uscita,utente,posto,ricarica,costo)VALUES ('" + prenotazioneConclusa.getId() + "','" + prenotazioneConclusa.getTempo_arrivo() + "','" + prenotazioneConclusa.getTempo_uscita() + "','" + prenotazioneConclusa.getUtente() + "','" + prenotazioneConclusa.getPosto() + "','" + ricarica + "', '" + lastId + "' );";
        dbStorico.update(insertPagamento);
    }
}
