import DataBase.DbPrenotazioni;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;


public class GestorePosti {
    private DbPrenotazioni dbPrenotazioni;
    private GestorePagamenti gestorePagamenti;
    private GestoreRicariche gestoreRicariche;
    DateTimeFormatter formatter;

    // Costruttore
    public GestorePosti(){
        this.dbPrenotazioni = new DbPrenotazioni();
        this.gestorePagamenti = new GestorePagamenti();
        this.gestoreRicariche = new GestoreRicariche();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    // Metodo per ottenere tutte le prenotazioni dal database
    public ArrayList<Prenotazioni> getPrenotazioni(){
        ArrayList<Prenotazioni> listaPrenotazioni = new ArrayList<>();
        String comandoSql = "SELECT * FROM Prenotazioni";
        System.out.println(comandoSql);

        // Esegue la query per ottenere tutte le prenotazioni dal database delle prenotazioni
        var rs = dbPrenotazioni.query(comandoSql);

        // Converte i risultati della query in oggetti Prenotazioni e li aggiunge alla lista
        for(HashMap<String, Object> record : rs){
            listaPrenotazioni.add(new Prenotazioni(record));
        }
        return listaPrenotazioni;
    }

    // Metodo per creare una nuova prenotazione nel database
    public Prenotazioni creaPrenotazione(Prenotazioni nuovaPrenotazione, int tipo, String provenienza) {
        // Se il tipo è 2 oppure se è un utente premium che occupa un posto, imposta il tempo di arrivo attuale
        if(tipo == 2 || (tipo == 1 && provenienza.equals("occupa"))){
            LocalDateTime now = LocalDateTime.now();
            nuovaPrenotazione.setTempo_arrivo(now.format(formatter));
        }

        // Ottiene gli ID dei posti auto disponibili e tutte le prenotazioni attuali
        ArrayList<Integer> idPostiAuto = this.getIdPostiAuto();
        ArrayList<Prenotazioni> prenotazioni = this.getPrenotazioni();

        // Verifica la disponibilità del posto per la nuova prenotazione
        if(this.verificaDisponibilta(idPostiAuto,prenotazioni,nuovaPrenotazione)){
            // Costruisce e esegue la query per inserire la nuova prenotazione nel database
            String comandoSql = "INSERT INTO Prenotazioni (tempo_arrivo, tempo_uscita, utente, posto,penale) VALUES ('" + nuovaPrenotazione.getTempo_arrivo().format(formatter) + "', '" + nuovaPrenotazione.getTempo_uscita().format(formatter) + "', '" + nuovaPrenotazione.getUtente() + "', '" + nuovaPrenotazione.getPosto() + "' , '0' );";
            System.out.println(comandoSql);

            // Se l'inserimento ha successo, aggiorna l'oggetto nuovaPrenotazione con i dati aggiornati e lo restituisce
            if(dbPrenotazioni.update(comandoSql)){
                nuovaPrenotazione = this.getPrenotazioneUsername(nuovaPrenotazione.getUtente());
                return nuovaPrenotazione;
            }
        }
        return null; // Ritorna null se non è possibile creare la prenotazione
    }

    // Metodo privato per ottenere gli ID dei posti auto dal database
    private ArrayList<Integer> getIdPostiAuto() {
        String comandoPostiAutoSql = "SELECT * FROM PostoAuto";
        System.out.println(comandoPostiAutoSql);
        ArrayList<Integer> idPostiAuto = new ArrayList<>();
        var rsPostiAuto = dbPrenotazioni.query(comandoPostiAutoSql);

        // Estrae gli ID dei posti auto dai risultati della query e li aggiunge alla lista
        for (HashMap<String, Object> postoAutoRs : rsPostiAuto) {
            idPostiAuto.add((int) postoAutoRs.get("id"));
        }
        return idPostiAuto;
    }

    // Metodo privato per verificare la disponibilità di un posto auto per una nuova prenotazione
    private boolean verificaDisponibilta(ArrayList<Integer> idPostiAuto, ArrayList<Prenotazioni> prenotazioni, Prenotazioni nuovaPrenotazione) {
        ArrayList<Integer> postiLiberi = new ArrayList<>(idPostiAuto);

        // Itera su tutte le prenotazioni attuali per verificare l'occupazione dei posti nel periodo richiesto
        for (Prenotazioni prenotazione : prenotazioni) {
            if (prenotazione.getTempo_arrivo().isBefore(nuovaPrenotazione.getTempo_uscita()) &&
                    prenotazione.getTempo_uscita().isAfter(nuovaPrenotazione.getTempo_arrivo())) {
                postiLiberi.remove((Integer) prenotazione.getPosto());
            }
        }

        // Se non ci sono posti liberi disponibili, ritorna false; altrimenti, assegna il primo posto libero alla nuova prenotazione
        if (postiLiberi.isEmpty()) {
            return false;
        } else {
            int postoAssegnato = postiLiberi.get(0);
            nuovaPrenotazione.setPosto(postoAssegnato);

            return true;
        }
    }

    // Metodo per ottenere una specifica prenotazione dal database dato il suo ID
    public Prenotazioni getPrenotazione(String id) {
        String comandoSql = "SELECT * FROM Prenotazioni WHERE id = \"" + id + "\"";
        System.out.println(comandoSql);
        var rs = dbPrenotazioni.query(comandoSql);

        // Se non esiste una prenotazione con l'ID specificato, ritorna null; altrimenti, restituisce l'oggetto Prenotazioni corrispondente
        if(rs.isEmpty())
            return null;

        return new Prenotazioni((Integer) rs.get(0).get("id"), LocalDateTime.parse((CharSequence) rs.get(0).get("tempo_arrivo"), formatter), LocalDateTime.parse((CharSequence) rs.get(0).get("tempo_uscita"), formatter), (String) rs.get(0).get("utente"), (Integer) rs.get(0).get("posto"),(Integer) rs.get(0).get("penale") == 1);

    }

    // Metodo per ottenere una prenotazione dato il nome utente
    public Prenotazioni getPrenotazioneUsername(String username) {
        String comandoSql = "SELECT * FROM Prenotazioni WHERE utente = \"" + username + "\"";
        System.out.println(comandoSql);
        var rs = dbPrenotazioni.query(comandoSql);

        // Se non esiste una prenotazione per l'utente specificato, ritorna null; altrimenti, restituisce l'oggetto Prenotazioni corrispondente
        if(rs.isEmpty())
            return null;

        return new Prenotazioni((Integer) rs.get(0).get("id"), LocalDateTime.parse((CharSequence) rs.get(0).get("tempo_arrivo"), formatter), LocalDateTime.parse((CharSequence) rs.get(0).get("tempo_uscita"), formatter), (String) rs.get(0).get("utente"), (Integer) rs.get(0).get("posto"),(Integer) rs.get(0).get("penale") == 1);

    }

    // Metodo per modificare una prenotazione esistente nel database
    public Prenotazioni modificaPrenotazione(Prenotazioni nuovaPrenotazione, Prenotazioni vecchiaPrenotazione) {

        // Se la prenotazione vecchia non esiste, ritorna null
        if(vecchiaPrenotazione == null) return null;

        // Ottiene gli ID dei posti auto disponibili e tutte le prenotazioni attuali, escludendo la prenotazione vecchia
        ArrayList<Integer> idPostiAuto = this.getIdPostiAuto();
        ArrayList<Prenotazioni> prenotazioni = this.getPrenotazioni();

        prenotazioni.removeIf(prenotazione -> prenotazione.getId() == vecchiaPrenotazione.getId());

        // Verifica la disponibilità del posto per la nuova prenotazione
        if(this.verificaDisponibilta(idPostiAuto,prenotazioni,nuovaPrenotazione)){
            // Costruisce e esegue la query per aggiornare la prenotazione nel database
            String comandoSql = "UPDATE Prenotazioni SET tempo_arrivo = \"" + nuovaPrenotazione.getTempo_arrivo().format(formatter) + "\", tempo_uscita = \"" + nuovaPrenotazione.getTempo_uscita().format(formatter) + "\" WHERE id = \"" + vecchiaPrenotazione.getId() + "\";";
            System.out.println(comandoSql);
            dbPrenotazioni.update(comandoSql);

            // Aggiorna l'oggetto vecchiaPrenotazione con i nuovi dati e lo restituisce
            vecchiaPrenotazione.setTempo_arrivo(nuovaPrenotazione.getTempo_arrivo().format(formatter));
            vecchiaPrenotazione.setTempo_uscita(nuovaPrenotazione.getTempo_uscita().format(formatter));
            return vecchiaPrenotazione;
        }
        return null; // Ritorna null se non è possibile modificare la prenotazione
    }

    // Metodo per cancellare una prenotazione dal database dato il suo ID
    public boolean cancellaPrenotazione(String id) {
        String comandoSql = "DELETE FROM Prenotazioni WHERE id = \"" + id + "\";";
        System.out.println(comandoSql);

        // Esegue la query per cancellare la prenotazione dal database e restituisce true se l'operazione ha successo, altrimenti false
        return dbPrenotazioni.update(comandoSql);
    }

    public ArrayList<HashMap<String, Object>> getStatoPosti() {
        return dbPrenotazioni.query("SELECT * FROM PostoAuto");
    }

    public void statoPosti(String topic, MqttMessage message) {
        Gson gson = new Gson();
        String payload = new String(message.getPayload());
        System.out.println("Messaggio sensore ricevuto su " + topic + ": " + payload);
        String idPosto = topic.split("/")[2];
        HashMap<String, String> stato = gson.fromJson(payload, new TypeToken<HashMap<String, String>>(){}.getType());

        if(stato.get("stato").equals("occupato")){//occupato
            // Dopo aver occupato il posto, viene segnato come occupato in modo tale che non venga utilizzato da altre prenotazioni
            String comandoSql = "UPDATE PostoAuto SET disponibilita = 1 WHERE id = \"" + idPosto + "\";";
            dbPrenotazioni.update(comandoSql);
        }
        else{//libero
            // Dopo aver liberato il posto, viene segnato come libero in modo tale che possa essere utilizzato da altre prenotazioni
            String comandoSql = "UPDATE PostoAuto SET disponibilita = 0 WHERE id = \"" + idPosto + "\";";
            dbPrenotazioni.update(comandoSql);

            Prenotazioni prenotazioneConclusa = this.getPrenotazioniIdPosto(idPosto).get(0);
            Ricariche ricaricaConclusa = gestoreRicariche.getRicaricheByPrenotazione(String.valueOf(prenotazioneConclusa.getId()));

            // Viene richiesto il pagamento andando ad utilizzare le informazioni presenti nella prenotazioni e la possibile ricarica
            gestorePagamenti.effettuaPagamento(prenotazioneConclusa,ricaricaConclusa);
        }
    }

    // Metodo per ottenere le prenotazioni dato l'id del posto
    public ArrayList<Prenotazioni> getPrenotazioniIdPosto(String idPosto) {
        ArrayList<Prenotazioni> listaPrenotazioni = new ArrayList<>();
        // Order by viene utilizzato in quanto dobbiamo prendere la prima prenotazione che è quella che sta finendo
        String comandoSql = "SELECT * FROM Prenotazioni WHERE posto = \"" + idPosto + "\" ORDER BY tempo_arrivo;";
        System.out.println(comandoSql);
        var rs = dbPrenotazioni.query(comandoSql);

        for(HashMap<String, Object> record : rs){
            listaPrenotazioni.add(new Prenotazioni(record));
        }
        return listaPrenotazioni;
    }

    // Metodo per aggiungere la penale ad una prenotazione in modo tale da considerarla durante il pagamento della sosta
    public void aggiungiPenalePrenotazione(String idParam) {
        String comandoSql = "UPDATE Prenotazioni SET penale = TRUE WHERE id = \"" + idParam + "\";";
        dbPrenotazioni.update(comandoSql);
    }


}
