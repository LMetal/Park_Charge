import DataBase.DbPrenotazioni;
import DataBase.DbRicariche;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class GestoreRicariche {
    private DbRicariche dbRicariche;
    private DbPrenotazioni dbPrenotazioni;
    private Gson gson;

    public GestoreRicariche(){
        this.dbPrenotazioni = new DbPrenotazioni();
        this.dbRicariche = new DbRicariche();
        this.gson = new Gson();
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

    public void addRicarica(int percentuale, int prenotazioneId){
        //add new charge
        dbRicariche.update("INSERT INTO Ricarica(prenotazione, percentuale_richiesta, percentuale_erogata, MWBot) VALUES ("+prenotazioneId+","+percentuale+", '0', '1');");

        this.publishNuovoTarget();
    }

    private void publishNuovoTarget() {
        HashMap<String,Object> comandoMWBot = new HashMap<>();

        //publish nuovo target
        var gestorePosti = new GestorePosti();
        Prenotazioni target = EDF.getJobPosto(gestorePosti.getPrenotazioni(), this.getRicariche());
        if(target == null) return;
        Ricariche ricarica = this.getRicaricaByPrenotazione(target.getId());
        if(ricarica == null) return;

        dbRicariche.update("UPDATE MWBot SET idPrenotazione = \"" + target.getId() + "\" WHERE id = 1");
        comandoMWBot.put("target", target.getPosto());
        comandoMWBot.put("percentualeRicarica", ricarica.getPercentuale_richiesta() - ricarica.getPercentuale_erogata());
        Backend.publish("ParkCharge/RichiediRicarica/1", gson.toJson(comandoMWBot));
    }

    public boolean stopRicaricaByPrenotazione(String id_prenotazione) {
        try{
            dbRicariche.update("DELETE FROM Ricarica WHERE prenotazione = \"" + id_prenotazione + "\";");
        } catch (Exception e){
            return false;
        }
        return true;
    }


    public Ricariche getRicaricheByPrenotazione(String id_prenotazione) {
        var ricaricaUtente = dbRicariche.query("SELECT * FROM Ricarica WHERE prenotazione = \""+ id_prenotazione + "\"");
        if(ricaricaUtente == null) return null;
        if(ricaricaUtente.size() != 1) return null;

        return new Ricariche(ricaricaUtente.get(0));
    }

    private Ricariche getRicaricaByPrenotazione(int ricaricaID) {
        var ricarica = dbRicariche.query("SELECT * FROM Ricarica WHERE prenotazione = "+ ricaricaID);
        if(ricarica == null) return null;
        if(ricarica.size() < 1) return null;

        return new Ricariche(ricarica.get(0));
    }

    public void statoRicariche(String topic, MqttMessage mqttMessage) {
        String payload = new String(mqttMessage.getPayload());
        System.out.println("Messaggio sensore ricevuto su " + topic + ": " + payload);

        String MWBotID = topic.split("/")[2];
        HashMap<String, String> MWBotJson = gson.fromJson(payload, new TypeToken<HashMap<String, String>>(){}.getType());

        //aggiorna stato database
        String stato = MWBotJson.get("statoCarica");
        dbRicariche.update("UPDATE MWBot SET stato = \""+ stato +"\" WHERE id = \"" + MWBotID + "\";");

        if(stato.equals("Charging")){
            //aggiorna percentuale erogata
            if(Float.parseFloat(MWBotJson.get("KW_Emessi")) == 0) return;
            int prenotazioneID = (int) dbRicariche.query("SELECT * FROM MWBot WHERE id = \""+ MWBotID + "\"").get(0).get("idPrenotazione");

            var r = this.getRicaricaByPrenotazione(prenotazioneID); //get ricarica in corso (associata alla prenotazione)

            if (r == null) {
                System.out.println("ERRORE");
                return;
            }

            int nuovaPrecentale = r.getPercentuale_erogata() + 1;
            dbRicariche.update("UPDATE Ricarica SET percentuale_erogata = \"" + nuovaPrecentale + "\" WHERE prenotazione = \""+ prenotazioneID +"\"");
        } else {
            //fine ricarica
            System.out.println("finita");

        }

    }


}
