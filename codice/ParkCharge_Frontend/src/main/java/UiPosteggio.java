import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import static javax.swing.JOptionPane.*;
import static javax.swing.JOptionPane.CLOSED_OPTION;

public class UiPosteggio {

    private String baseURL = "http://localhost:4568/api/v1.0";

    //Attributi
    private String tempo_uscita;
    private String tempo_arrivo;
    private int scelta;
    private boolean formato;
    private boolean indietro;
    private HashMap<String,Object> prenotazione;
    private Gson gson;

    //Elementi grafici
    private JLabel tempoUscitaOccupaLabel;
    private JTextField tempoUscitaOccupaField;
    private JPanel occupaPanel;
    private String[] pulsantiOccupa;
    private JLabel tempoUscitaPrenotaLabel;
    private JTextField tempoUscitaPrenotaField;
    private JLabel tempoArrivoPrenotaLabel;
    private JTextField tempoArrivoPrenotaField;
    private JPanel prenotaPanel;
    private String[] pulsantiPrenota;
    private JLabel tempoUscitaModificaLabel;
    private JLabel tempoArrivoModificaLabel;
    private JTextField tempoUscitaModificaField;
    private JTextField tempoArrivoModificaField;
    private JPanel modificaPanel;
    private String[] pulsantiModifica;
    private JLabel cancellaLabel;
    private JPanel cancellaPanel;
    private String[] pulsantiCancella;


    public UiPosteggio(){
        tempoUscitaOccupaLabel = new JLabel("Tempo Uscita");
        tempoUscitaOccupaField = new JTextField("",15);
        occupaPanel = new JPanel(new GridLayout(1,1));
        occupaPanel.add(tempoUscitaOccupaLabel);
        occupaPanel.add(tempoUscitaOccupaField);
        pulsantiOccupa = new String[2];
        pulsantiOccupa[0] = "Indietro";
        pulsantiOccupa[1] = "Occupa posto";

        tempoUscitaPrenotaLabel = new JLabel("Tempo Uscita");
        tempoUscitaPrenotaField = new JTextField("",15);
        tempoArrivoPrenotaLabel = new JLabel("Tempo Arrivo");
        tempoArrivoPrenotaField = new JTextField("",15);
        prenotaPanel = new JPanel(new GridLayout(2,2));
        prenotaPanel.add(tempoArrivoPrenotaLabel);
        prenotaPanel.add(tempoArrivoPrenotaField);
        prenotaPanel.add(tempoUscitaPrenotaLabel);
        prenotaPanel.add(tempoUscitaPrenotaField);
        pulsantiPrenota = new String[2];
        pulsantiPrenota[0] = "Indietro";
        pulsantiPrenota[1] = "Prenota posto";

        tempoUscitaModificaLabel = new JLabel("Tempo Uscita");
        tempoUscitaModificaField = new JTextField("",15);
        tempoArrivoModificaLabel = new JLabel("Tempo Arrivo");
        tempoArrivoModificaField = new JTextField("",15);
        modificaPanel = new JPanel(new GridLayout(3,2));
        modificaPanel.add(tempoArrivoModificaLabel);
        modificaPanel.add(tempoArrivoModificaField);
        modificaPanel.add(tempoUscitaModificaLabel);
        modificaPanel.add(tempoUscitaModificaField);
        pulsantiModifica = new String[3];
        pulsantiModifica[0] = "Indietro";
        pulsantiModifica[1] = "Modifica";
        pulsantiModifica[2] = "Elimina";

        cancellaLabel = new JLabel("Confermi di voler cancellare la prenotazione?");
        cancellaPanel = new JPanel(new GridLayout(1,1));
        cancellaPanel.add(cancellaLabel);
        pulsantiCancella = new String[2];
        pulsantiCancella[0] = "Indietro";
        pulsantiCancella[1] = "Conferma";


        prenotazione = new HashMap<>();
        gson = new Gson();
    }

    public HashMap<String,Object>  avviaOccupaPosto(HashMap<String,Object> utente) {
        indietro = false;
        formato = false;

        HashMap<String, Object> prenotazioneAttiva = this.controllaPrenotazioneAttiva(utente,false);
        if(prenotazioneAttiva != null){
            return prenotazioneAttiva;
        }

        while(!indietro && !formato) {
            this.mostraFormOccupaPosto();
            if (!indietro && formato) {
                try{
                    this.prenotazione.put("utente", utente.get("username"));
                    this.prenotazione.put("tempo_uscita", tempo_uscita);
                    String prenotazioneJson = gson.toJson(this.prenotazione);

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(baseURL + "/prenotazioni/" + utente.get("username")))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(prenotazioneJson))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if(response.statusCode() == 201){
                        prenotazione = gson.fromJson(response.body(), new TypeToken<HashMap<String,Object>>() {}.getType());
                        JOptionPane.showMessageDialog(null, "Ben arrivato, il posto a te assegnato e il numero: " + prenotazione.get("posto"), "Successo", INFORMATION_MESSAGE);
                        return prenotazione;
                    }
                    else{
                        showMessageDialog(null,response.body(), "Errore", ERROR_MESSAGE);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void mostraFormOccupaPosto() {
        scelta = showOptionDialog(null, occupaPanel, "Occupa posto (clicca su X per uscire)", DEFAULT_OPTION, QUESTION_MESSAGE, null, pulsantiOccupa, "Occupa posto");

        if (scelta == 0 || scelta == CLOSED_OPTION) // torna indietro
        {
            tempoUscitaOccupaField.setText("");
            indietro = true;
        }
        if (scelta == 1) // Occupa posto
        {
            tempo_uscita = tempoUscitaOccupaField.getText();
            formato = this.controlloFormatoTempo();
            tempoUscitaOccupaField.setText("");
        }
    }

    private boolean controlloFormatoTempo() {
        String regex = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
        if (!Pattern.matches(regex, tempo_uscita)) {
            showMessageDialog(null, "Usa il formato yyyy-MM-dd HH:mm:ss.", "Errore", ERROR_MESSAGE);
            return false;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime inputDateTime = LocalDateTime.parse(tempo_uscita, formatter);
        LocalDateTime now = LocalDateTime.now();

        if (inputDateTime.isAfter(now)) {
            return true;
        } else {
            showMessageDialog(null, "La data e ora devono essere maggiori del tempo attuale.", "Errore", ERROR_MESSAGE);
            return false;
        }

    }

    public HashMap<String,Object> avviaPrenotaPosto(HashMap<String,Object> utente) {
        indietro = false;
        formato = false;

        HashMap<String, Object> prenotazioneAttiva = this.controllaPrenotazioneAttiva(utente,true);
        if(prenotazioneAttiva != null){
            return prenotazioneAttiva;
        }

        while(!indietro && !formato) {
            this.mostraFormPrenotaPosto();
            if(!indietro && formato) {
                try{
                    this.prenotazione.put("utente", utente.get("username"));
                    this.prenotazione.put("tempo_arrivo", tempo_arrivo);
                    this.prenotazione.put("tempo_uscita", tempo_uscita);
                    String prenotazioneJson = gson.toJson(this.prenotazione);

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(baseURL + "/prenotazioni/premium/" + utente.get("username")))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(prenotazioneJson))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if(response.statusCode() == 201){
                        prenotazione = gson.fromJson(response.body(), new TypeToken<HashMap<String,Object>>() {}.getType());
                        JOptionPane.showMessageDialog(null, "Prenotazione effettuata con successo", "Successo", INFORMATION_MESSAGE);
                        return prenotazione;
                    }
                    else{
                        showMessageDialog(null,response.body(), "Errore", ERROR_MESSAGE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void mostraFormPrenotaPosto() {
        scelta = showOptionDialog(null, prenotaPanel, "Prenota posto (clicca su X per uscire)", DEFAULT_OPTION, QUESTION_MESSAGE, null, pulsantiPrenota, "Prenota posto");

        if (scelta == 0 || scelta == CLOSED_OPTION) // torna indietro
        {
            tempoArrivoPrenotaField.setText("");
            tempoUscitaPrenotaField.setText("");
            indietro = true;
        }
        if (scelta == 1) // Occupa posto
        {
            tempo_arrivo = tempoArrivoPrenotaField.getText();
            tempo_uscita = tempoUscitaPrenotaField.getText();
            formato = this.controlloFormatoTempi();
            tempoArrivoPrenotaField.setText("");
            tempoUscitaPrenotaField.setText("");
        }
    }

    private boolean controlloFormatoTempi() {
        String regex = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";

        if (!Pattern.matches(regex, tempo_arrivo) || !Pattern.matches(regex, tempo_uscita)) {
            showMessageDialog(null, "Usa il formato yyyy-MM-dd HH:mm:ss.", "Errore", ERROR_MESSAGE);
            return false;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime arrivoDateTime = LocalDateTime.parse(tempo_arrivo, formatter);
        LocalDateTime uscitaDateTime = LocalDateTime.parse(tempo_uscita, formatter);
        LocalDateTime now = LocalDateTime.now();

        if (arrivoDateTime.isAfter(now) && uscitaDateTime.isAfter(now) && uscitaDateTime.isAfter(arrivoDateTime)) {
            return true;
        } else {
            showMessageDialog(null, "La data e ora devono essere maggiori del tempo attuale e il tempo di uscita deve essere maggiore del tempo di arrivo.", "Errore", ERROR_MESSAGE);
            return false;
        }
    }

    public HashMap<String,Object> controllaPrenotazioneAttiva(HashMap<String,Object> utente, boolean provenienza) { //True corrisponde alla provenienza da prenotaPosto, altrimenti da occupaPosto

        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/prenotazioni"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200){
                ArrayList<HashMap<String, Object>> prenotazioni = gson.fromJson(response.body(), new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
                for(HashMap<String,Object> prenoto : prenotazioni){
                    if(prenoto.get("utente").equals(utente.get("username"))){
                        if(utente.get("tipo").equals("1")){
                            if(provenienza)
                                JOptionPane.showMessageDialog(null,"Spiacente, hai gia una prenotazione attiva","Errore", ERROR_MESSAGE);
                            else
                                JOptionPane.showMessageDialog(null, "Ben arrivato, il posto a te assegnato e il numero: " + prenoto.get("posto"), "Successo", INFORMATION_MESSAGE);
                        }
                        else
                            JOptionPane.showMessageDialog(null,"Spiacente, stai gia occupando il posto: " + prenoto.get("posto"), "Errore", ERROR_MESSAGE);
                        return prenoto;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String,Object> avviaModificaPrenotazione(HashMap<String,Object> utente, HashMap<String,Object> prenotazioneModifica) {
        indietro = false;
        formato = false;
        if(prenotazioneModifica == null || prenotazioneModifica.get("id") == null)
            prenotazioneModifica = this.getPrenotazioneUtente(utente);

        if(prenotazioneModifica == null || prenotazioneModifica.get("id") == null){
            JOptionPane.showMessageDialog(null,"Spiacente, non hai prenotazioni da modificare", "Errore", ERROR_MESSAGE);
            return null;
        }
        this.prenotazione = prenotazioneModifica;


        while(!indietro && !formato) {
            this.mostraFormModificaPrenotazione(prenotazioneModifica);
            if (!indietro && formato) {
                try {
                    this.prenotazione.put("tempo_arrivo", tempo_arrivo);
                    this.prenotazione.put("tempo_uscita", tempo_uscita);
                    String prenotazioneJson = gson.toJson(this.prenotazione);

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(baseURL + "/prenotazioni/modifica/" + prenotazioneModifica.get("id")))
                            .header("Content-Type", "application/json")
                            .PUT(HttpRequest.BodyPublishers.ofString(prenotazioneJson))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if(response.statusCode() == 200){
                        prenotazione = gson.fromJson(response.body(), new TypeToken<HashMap<String, Object>>() {}.getType());
                        JOptionPane.showMessageDialog(null, "Prenotazione modificata con successo", "Successo", INFORMATION_MESSAGE);
                        return prenotazione;
                    }
                    else{
                        showMessageDialog(null,response.body(), "Errore", ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void mostraFormModificaPrenotazione(HashMap<String,Object> prenotazioneModifica) {
        scelta = showOptionDialog(null, modificaPanel, "Modifca prenotazione (clicca su X per uscire)", DEFAULT_OPTION, QUESTION_MESSAGE, null, pulsantiModifica, "Modifica prenotazione");

        if (scelta == 0 || scelta == CLOSED_OPTION) // torna indietro
        {
            tempoArrivoModificaField.setText("");
            tempoUscitaModificaField.setText("");
            indietro = true;
        }
        if (scelta == 1) // Modica prenotazione
        {
            tempo_arrivo = tempoArrivoModificaField.getText();
            tempo_uscita = tempoUscitaModificaField.getText();
            formato = this.controlloFormatoTempi();
            tempoArrivoModificaField.setText("");
            tempoUscitaModificaField.setText("");
        }
        if(scelta == 2){ // Cancella prenotazione
            this.avviaCancellaPrenotazione(prenotazioneModifica);
            indietro = true;
        }
    }

    private void avviaCancellaPrenotazione(HashMap<String, Object> prenotazioneModifica) {
        this.prenotazione = prenotazioneModifica;
        scelta = showOptionDialog(null, cancellaPanel, "Cancella prenotazione (clicca su X per uscire)", DEFAULT_OPTION, QUESTION_MESSAGE, null, pulsantiCancella, "Cancella prenotazione");

        if(scelta == 1){
            try{
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(baseURL + "/prenotazioni/" + prenotazioneModifica.get("id")))
                        .header("Content-Type", "application/json")
                        .DELETE()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if(response.statusCode() == 204)
                    JOptionPane.showMessageDialog(null, "Prenotazione cancellata con successo", "Successo", INFORMATION_MESSAGE);
                else
                    showMessageDialog(null,response.body(), "Errore", ERROR_MESSAGE);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public HashMap<String,Object> getPrenotazioneUtente(HashMap<String,Object> utente) {

        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/prenotazioni/" + utente.get("username")))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200){
                prenotazione = gson.fromJson(response.body(), new TypeToken<HashMap<String, Object>>() {}.getType());
                return prenotazione;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
