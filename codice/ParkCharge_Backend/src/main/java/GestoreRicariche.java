import DataBase.DbRicariche;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashMap;

public class GestoreRicariche {
    private DbRicariche dbRicariche;
    private GestorePosti gestorePosti;
    private Gson gson;

    public GestoreRicariche(){
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
        
        Prenotazioni target = EDF.getJobPosto(gestorePosti.getPrenotazioni(), this.getRicariche(), false);
        if(target == null){
            dbRicariche.update("UPDATE MWBot SET idPrenotazione = \"-1\", stato = \"Finito\" WHERE id = 1");
            comandoMWBot.put("target", -1);
            comandoMWBot.put("percentualeRicarica", 0);
            Backend.publish("ParkCharge/RichiediRicarica/1", gson.toJson(comandoMWBot));
            return;
        }
        var pr = this.getRicaricheByPrenotazione(target.getId());
        Ricariche ricarica = pr.stream()
                .filter(r -> r.getPercentuale_erogata() < r.getPercentuale_richiesta())
                .findFirst()
                .orElse(null);
        if(ricarica == null) return;

        dbRicariche.update("UPDATE MWBot SET idPrenotazione = \"" + target.getId() + "\", stato = \"Charging\" WHERE id = 1");
        comandoMWBot.put("target", target.getPosto());
        comandoMWBot.put("percentualeRicarica", ricarica.getPercentuale_richiesta() - ricarica.getPercentuale_erogata());
        Backend.publish("ParkCharge/RichiediRicarica/1", gson.toJson(comandoMWBot));
        System.out.println("HERE");
    }

    /**
     * @param id_prenotazione prenotazione di cui fermare la ricarica in corso
     * @return True se esecuzione corretta, False altrimenti
     * La ricarica viene ritenuta completata quando la percentuale_emessa corrisponde alla percentuale_richiesta,
     * viene quindi impostata la percentuale_richiesta a quanto emesso fino a ora.
     * Viene comunicato il nuovo target all'MWBot.
     */
    public boolean stopRicaricaByPrenotazione(String id_prenotazione) {
        try{
            var ricaricaDaInterrompere = dbRicariche.query("SELECT * FROM Ricarica WHERE prenotazione = \"" + id_prenotazione + "\" AND percentuale_richiesta != percentuale_erogata;");
            float percentualeRicaricata = Float.parseFloat(ricaricaDaInterrompere.get(0).get("percentuale_erogata").toString());
            dbRicariche.update("UPDATE Ricarica SET percentuale_richiesta = percentuale_erogata WHERE prenotazione = \"" + id_prenotazione + "\" AND percentuale_richiesta != percentuale_erogata;");
            this.notificaRicaricaConclusa(percentualeRicaricata);
            this.publishNuovoTarget();

        } catch (Exception e){
            System.out.println("ERRORE stop line 89: " + e.getMessage());
            return false;
        }
        return true;
    }


    public ArrayList<Ricariche> getRicaricheByPrenotazione(String id_prenotazione) {
        ArrayList<Ricariche> ricaricheList = new ArrayList<>();

        var ricarichePrenotazione = dbRicariche.query("SELECT * FROM Ricarica WHERE prenotazione = \""+ id_prenotazione + "\"");
        if(ricarichePrenotazione == null) return null;
        if(ricarichePrenotazione.size() < 1) return null;

        for(HashMap<String, Object> r : ricarichePrenotazione){
            ricaricheList.add(new Ricariche(r));
        }

        return ricaricheList;
    }

    private ArrayList<Ricariche> getRicaricheByPrenotazione(int id_prenotazione) {
        return this.getRicaricheByPrenotazione(String.valueOf(id_prenotazione));
    }

    public void statoRicariche(String topic, MqttMessage mqttMessage) {
        String payload = new String(mqttMessage.getPayload());
        System.out.println("Messaggio MWBot ricevuto su " + topic + ": " + payload);

        String MWBotID = topic.split("/")[2];
        HashMap<String, String> MWBotJson = gson.fromJson(payload, new TypeToken<HashMap<String, String>>(){}.getType());

        //aggiorna stato database
        String stato = MWBotJson.get("statoCarica");
        float KWEmessi = Float.parseFloat(MWBotJson.get("KW_Emessi"));
        dbRicariche.update("UPDATE MWBot SET stato = \""+ stato +"\" WHERE id = \"" + MWBotID + "\";");


        int prenotazioneID = (int) dbRicariche.query("SELECT * FROM MWBot WHERE id = \""+ MWBotID + "\"").get(0).get("idPrenotazione");

        if(stato.equals("Charging")){
            Ricariche ric = this.getRicaricheByPrenotazione(prenotazioneID).stream()
                    .filter(r -> r.getPercentuale_erogata() < r.getPercentuale_richiesta())
                    .findFirst()
                    .orElse(null); //get ricarica in corso (associata alla prenotazione)

            if (ric == null) {
                System.out.println("ERRORE");
                return;
            }

            //aggiorna percentuale erogata

            if(KWEmessi == 0) return;

            int nuovaPrecentuale = ric.getPercentuale_erogata() + 1;
            dbRicariche.update("UPDATE Ricarica SET percentuale_erogata = \"" + nuovaPrecentuale + "\" WHERE prenotazione = \""+ prenotazioneID +"\" AND percentuale_richiesta != percentuale_erogata");
        } else {
            //fine ricarica
            System.out.println("finita");
            this.notificaRicaricaConclusa(KWEmessi);
        }
    }
    private void notificaRicaricaConclusa(float KWEmessi){
        HashMap<String,Object> json  = new HashMap<>();
        GestorePagamenti gestorePagamenti = new GestorePagamenti();
        GestorePosti gestorePosti = new GestorePosti();
        var costi = gestorePagamenti.getCosti().get(0);
        float costoAlKW = Float.parseFloat(costi.get("costo_ricarica").toString());
        System.out.println(costoAlKW);

        int prenotazioneID = (int) dbRicariche.query("SELECT * FROM MWBot WHERE id = 1").get(0).get("idPrenotazione");
        Prenotazioni p = gestorePosti.getPrenotazione(Integer.toString(prenotazioneID));
        System.out.println(p);



        json.put("kilowattUsati", KWEmessi);
        json.put("costoRicarica", KWEmessi * costoAlKW);
        Backend.publish("ParkCharge/Notifiche/RicaricaConclusa/" + p.getUtente(), gson.toJson(json));
    }


}
