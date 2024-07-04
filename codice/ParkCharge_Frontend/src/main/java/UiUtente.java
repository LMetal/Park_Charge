import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import static javax.swing.JOptionPane.*;

public class UiUtente {

    private String baseURL = "http://localhost:4568/api/v1.0";

    //Elementi Grafici
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel nomeLabel;
    private JLabel cognomeLabel;
    private JLabel cartaLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField cartaField;
    private JPanel registrazionePanel;
    private String[] pulsantiRegistrazione;
    private String[] pulsantiPremium;
    private JLabel premiumLabel;
    private JPanel premiumPanel;
    private JPanel modificaDatiPanel;
    private String[] pulsantiModificaDati;
    private JLabel nomeModificaLabel;
    private JLabel cognomeModificaLabel;
    private JLabel cartaModificaLabel;
    private JTextField nomeModificaField;
    private JTextField cognomeModificaField;
    private JTextField cartaModificaField;


    //Attributii
    private String username;
    private String password;
    private String nome;
    private String cognome;
    private String carta;
    private HashMap<String,Object> utente;
    private int scelta;
    private boolean indietro;
    private boolean formato;
    private Gson gson;

    // Costruttore
    public UiUtente(){
        usernameLabel = new JLabel("Username");
        passwordLabel = new JLabel("Password");
        nomeLabel = new JLabel("Nome");
        cognomeLabel = new JLabel("Cognome");
        cartaLabel = new JLabel("Carta");
        usernameField = new JTextField("");
        passwordField = new JPasswordField("");
        nomeField = new JTextField("");
        cognomeField = new JTextField("");
        cartaField = new JTextField("");
        registrazionePanel = new JPanel(new GridLayout(5,2));
        registrazionePanel.add(usernameLabel);
        registrazionePanel.add(usernameField);
        registrazionePanel.add(nomeLabel);
        registrazionePanel.add(nomeField);
        registrazionePanel.add(cognomeLabel);
        registrazionePanel.add(cognomeField);
        registrazionePanel.add(cartaLabel);
        registrazionePanel.add(cartaField);
        registrazionePanel.add(passwordLabel);
        registrazionePanel.add(passwordField);
        pulsantiRegistrazione = new String[2];
        pulsantiRegistrazione[0] = "Indietro";
        pulsantiRegistrazione[1] = "Crea";

        premiumLabel = new JLabel("Confermi di voler diventare premium?");
        pulsantiPremium= new String[2];
        pulsantiPremium[0] = "Indietro";
        pulsantiPremium[1] = "Conferma";
        premiumPanel = new JPanel(new GridLayout(1,1));
        premiumPanel.add(premiumLabel);

        pulsantiModificaDati = new String[2];
        pulsantiModificaDati[0] = "Indietro";
        pulsantiModificaDati[1] = "Modifica";
        nomeModificaLabel = new JLabel("Nome");
        cognomeModificaLabel = new JLabel("Cognome");
        cartaModificaLabel = new JLabel("Carta");
        nomeModificaField = new JTextField("");
        cognomeModificaField = new JTextField("");
        cartaModificaField = new JTextField("");
        modificaDatiPanel = new JPanel(new GridLayout(3,2));
        modificaDatiPanel.add(nomeModificaLabel);
        modificaDatiPanel.add(nomeModificaField);
        modificaDatiPanel.add(cognomeModificaLabel);
        modificaDatiPanel.add(cognomeModificaField);
        modificaDatiPanel.add(cartaModificaLabel);
        modificaDatiPanel.add(cartaModificaField);

        utente = new HashMap<>();
        gson = new Gson();
    }


    // Metodo per avviare la modifica dei dati dell'utente
    public HashMap<String,Object> avviaModificaDati(HashMap<String,Object> utente) {
        indietro = false;
        formato = false;
        this.utente = utente;

        while(!indietro && !formato) {
            this.mostraFormModificaDati();
            if (!indietro && formato) {
                try{
                    // Aggiorna i dati dell'utente
                    utente.put("nome", nome);
                    utente.put("cognome", cognome);
                    utente.put("carta", carta);

                    String utenteModificato = gson.toJson(utente);

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(baseURL + "/utenti/" + utente.get("username")))
                            .header("Content-Type", "application/json")
                            .PUT(HttpRequest.BodyPublishers.ofString(utenteModificato))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if(response.statusCode() == 200) {
                        JOptionPane.showMessageDialog(null, "La modifica dei dati e avvenuta correttamente", "Successo", INFORMATION_MESSAGE);
                        return this.utente;
                    }else{
                        JOptionPane.showMessageDialog(null, response.body(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return utente;
    }

    // Metodo per mostrare il form per la modifica dei dati
    private void mostraFormModificaDati() {
        scelta = showOptionDialog(null, modificaDatiPanel, "Modifica dati (clicca su X per uscire)", DEFAULT_OPTION, QUESTION_MESSAGE, null, pulsantiModificaDati, "Modifica dati");

        if (scelta == 0 || scelta == CLOSED_OPTION) // torna indietro
        {
            svuotaCampi(new ArrayList<>(Arrays.asList(nomeModificaField,cognomeModificaField,cartaModificaField)));
            indietro = true;
        }
        if (scelta == 1) // Modifica Dati
        {
            carta = cartaModificaField.getText();
            nome = nomeModificaField.getText();
            cognome = cognomeModificaField.getText();
            formato = this.controlloFormato();
            svuotaCampi(new ArrayList<>(Arrays.asList(nomeModificaField,cognomeModificaField,cartaModificaField)));
        }
    }

    // Metodo per avviare il processo per diventare utente premium
    public HashMap<String,Object> avviaDiventaPremium(HashMap<String,Object> utente) {
        this.utente = utente;
        scelta = showOptionDialog(null, premiumPanel, "Diventa Premium (clicca su X per uscire)", DEFAULT_OPTION, QUESTION_MESSAGE, null, pulsantiPremium, "Diventa Premium");

        if (scelta == 1) // Diventa Premium
        {
            try{
                // Effettua la richiesta API REST per aggiornare il tipo di utente a premium
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(baseURL + "/utenti/tipo/" + utente.get("username")))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if(response.statusCode() == 200) {
                    JOptionPane.showMessageDialog(null, "Ti e stato addebitato un costo di " + response.body() + " euro sulla carta", "Successo", INFORMATION_MESSAGE);
                    this.utente.put("tipo", "1");
                    return this.utente;
                }
                else{
                    JOptionPane.showMessageDialog(null, response.body(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return utente;
    }

    // Metodo per avviare il processo di creazione di un nuovo utente
    public HashMap<String,Object> avviaCreaUtente() {
        indietro = false;
        formato = false;

        while(!indietro && !formato){
            this.mostraFormRegistrazione();
            if(!indietro && formato){
                try{
                    // Crea un nuovo utente
                    utente.put("nome", nome);
                    utente.put("cognome", cognome);
                    utente.put("username", username);
                    utente.put("tipo", "2"); // Tipo 2 corrisponde a un utente normale
                    utente.put("carta", carta);
                    utente.put("password", password);

                    String utenteCreato = gson.toJson(utente);

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(baseURL + "/utenti"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(utenteCreato))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if(response.statusCode() == 201) {
                        JOptionPane.showMessageDialog(null, "Utente creato con successo", "Successo", INFORMATION_MESSAGE);
                        return utente;
                    }
                    else{
                        JOptionPane.showMessageDialog(null, response.body(), "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    // Metodo per mostrare il form per la registrazione di un nuovo utente
    private void mostraFormRegistrazione() {
        scelta = showOptionDialog(null, registrazionePanel, "Crea nuovo utente (clicca su X per uscire)", DEFAULT_OPTION, QUESTION_MESSAGE, null, pulsantiRegistrazione, "Crea utente");

        if (scelta == 0 || scelta == CLOSED_OPTION) // torna indietro
        {
            svuotaCampi(new ArrayList<>(Arrays.asList(nomeField,cognomeField,cartaField,usernameField,passwordField)));
            indietro = true;
        }
        if (scelta == 1) // Crea Utente
        {
            username = usernameField.getText();
            password = new String(passwordField.getPassword());
            carta = cartaField.getText();
            nome = nomeField.getText();
            cognome = cognomeField.getText();
            formato = this.controlloFormato();
            svuotaCampi(new ArrayList<>(Arrays.asList(nomeField,cognomeField,cartaField,usernameField,passwordField)));
        }
    }

    // Metodo per controllare il formato dei dati inseriti dall'utente
    private boolean controlloFormato() {
        if(nome.isEmpty() || cognome.isEmpty() || carta.isEmpty()){
            JOptionPane.showMessageDialog(null, "Tutti i campi devono essere completati", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Requisito non funzionale 12
        if (username.length() < 4) {
            JOptionPane.showMessageDialog(null, "L'username deve essere di almeno 4 caratteri", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Requisito non funzionale 13
        if(password.length() < 6 || !Pattern.compile("[0-9]").matcher(password).find()){
            JOptionPane.showMessageDialog(null, "La password deve essere di almeno 6 caratteri e contenere almeno un numero", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    // Metodo per svuotare i campi di input dei dati
    private void svuotaCampi(ArrayList<JTextField> textFields){
        for(JTextField textField: textFields){
            textField.setText("");
        }
    }
}
