import DataBase.DbPrenotazioni;
import DataBase.DbStorico;
import com.google.gson.Gson;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class GestorePagamenti {
    private DbStorico dbStorico;
    private DbPrenotazioni dbPrenotazioni;
    DateTimeFormatter formatter;

    // Costruttore
    public GestorePagamenti(){
        this.dbStorico = new DbStorico();
        this.dbPrenotazioni = new DbPrenotazioni();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    // Metodo per ottenere tutti i costi dal database
    public ArrayList<HashMap<String, Object>> getCosti(){
        String comandoSql = "SELECT * FROM Costi";

        // Esegue la query per ottenere i costi dal database storico
        var rs = dbStorico.query(comandoSql);
        return rs;
    }

    // Metodo per ottenere il costo del servizio premium dal database
    public int getCostoPremium() {
        String comandoSql = "SELECT costo_premium FROM Costi";

        // Esegue la query per ottenere il costo del servizio premium dal database storico
        ArrayList<HashMap<String, Object>> rs = dbStorico.query(comandoSql);

        // Restituisce il costo premium come intero
        return (Integer) rs.get(0).get("costo_premium");
    }

    // Metodo per aggiornare i prezzi nel database
    public boolean aggiornaPrezzi(Costi costo) {
        // Costruisce la query di aggiornamento dei prezzi con i dati forniti dall'oggetto Costi
        String comandoSql = "UPDATE Costi SET costo_posteggio = \"" + costo.getCosto_posteggio() + "\", costo_ricarica = \"" + costo.getCosto_ricarica() + "\", penale = \"" + costo.getPenale() + "\", costo_premium = \"" + costo.getCosto_premium() + "\" WHERE id = \"" + 1 + "\";";

        // Esegue la query di aggiornamento nel database storico e restituisce il risultato
        return dbStorico.update(comandoSql);
    }

    public ArrayList<HashMap<String, Object>> getStorico() {
        String comandoSql = "SELECT * FROM Pagamenti \n" +
                "JOIN PrezzoPosteggio \n" +
                "WHERE Pagamenti.costo = PrezzoPosteggio.id";

        return dbStorico.query(comandoSql);
    }

    public void effettuaPagamento(Prenotazioni prenotazioneConclusa, ArrayList<Ricariche> ricaricaConclusa) {
        HashMap<String, Object> costiAttuali = this.getCosti().get(0);

        // Calcolo del costo del parcheggio
        Duration tempoSosta = Duration.between(prenotazioneConclusa.getTempo_arrivo(), prenotazioneConclusa.getTempo_uscita());
        float costoPosteggio = ((Number) costiAttuali.get("costo_posteggio")).floatValue();
        float minutiSosta = (float) tempoSosta.toMinutes();
        float costoSosta = minutiSosta * costoPosteggio;

        // Gestione della penale
        int penale = prenotazioneConclusa.getPenale() ? ((Number) costiAttuali.get("penale")).intValue() : 0;
        costoSosta += penale;

        // Arrotondamento a due cifre decimali
        costoSosta = Math.round(costoSosta * 100.0f) / 100.0f;

        // Calcolo del costo di ricarica (se applicabile)
        float costoRicarica = 0;
        int kilowattUsati = 0;
        if(ricaricaConclusa != null && !ricaricaConclusa.isEmpty()){
            for (Ricariche richariche : ricaricaConclusa) {
                kilowattUsati += richariche.getPercentuale_erogata(); // 1% = 1 kW
            }
        }
        costoRicarica = kilowattUsati * ((Number) costiAttuali.get("costo_ricarica")).floatValue();

        // Creazione del JSON per la notifica
        HashMap<String, Object> prezzoPosteggio = new HashMap<>();
        prezzoPosteggio.put("tempoSosta", minutiSosta);
        prezzoPosteggio.put("costoSosta", costoSosta);
        prezzoPosteggio.put("kilowattUsati", kilowattUsati);
        prezzoPosteggio.put("costoRicarica", costoRicarica);

        Gson gson = new Gson(); // Publish sul device dell'utente con il pagamento
        Backend.publish("ParkCharge/Notifiche/SostaConclusa/" + prenotazioneConclusa.getUtente(), gson.toJson(prezzoPosteggio));

        // Viene spostata la prenotazione nello storico andando a segnare anche il prezzo del posteggio
        String deletConclusa = "DELETE FROM Prenotazioni WHERE id = \"" + prenotazioneConclusa.getId() + "\";";
        dbPrenotazioni.update(deletConclusa);

        String insertPrezzoPosteggio = "INSERT INTO PrezzoPosteggio (costo_posteggio,costo_ricarica,penale) VALUES ('" + costoSosta + "','" + costoRicarica + "','" + penale + "');";
        dbStorico.update(insertPrezzoPosteggio);

        // L'id essendo auto increment, è necessario questo tipo di select
        String selectLastId = "SELECT * FROM PrezzoPosteggio ORDER BY id DESC LIMIT 1";
        var rs = dbStorico.query(selectLastId);
        int lastId =(int) rs.get(0).get("id");

        int idRicarica = ricaricaConclusa != null ? ricaricaConclusa.get(0).getPrenotazione() : 0;
        String insertPagamento = "INSERT INTO Pagamenti (id,tempo_arrivo,tempo_uscita,utente,posto,ricarica,costo)VALUES ('" + prenotazioneConclusa.getId() + "','" + prenotazioneConclusa.getTempo_arrivo().format(formatter) + "','" + prenotazioneConclusa.getTempo_uscita().format(formatter) + "','" + prenotazioneConclusa.getUtente() + "','" + prenotazioneConclusa.getPosto() + "','" + idRicarica + "', '" + lastId + "' );";
        dbStorico.update(insertPagamento);
    }
}
