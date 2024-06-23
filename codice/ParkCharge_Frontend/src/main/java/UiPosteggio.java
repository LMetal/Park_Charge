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
            formato = this.controlloFormatoOccupa();
            tempoUscitaOccupaField.setText("");
        }
    }

    private boolean controlloFormatoOccupa() {
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

    public void avviaModificaPrenotazione() {
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
            formato = this.controlloFormatoPrenota();
            tempoArrivoPrenotaField.setText("");
            tempoUscitaPrenotaField.setText("");
        }
    }

    private boolean controlloFormatoPrenota() {
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
}
