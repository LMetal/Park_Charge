import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import static javax.swing.JOptionPane.*;

public class UiLogin {

    private String baseURL = "http://localhost:4568/api/v1.0";

    //Componenti
    private UiMonitora uiMonitora;
    private UiPosteggio uiPosteggio;
    private UiRicarica uiRicarica;
    private UiUtente uiUtente;

    //Attributii
    private int scelta;
    private int sceltaMenu;
    private String username;
    private String password;
    private String esitoControllo;
    private String esitoRicerca;
    private HashMap<String,Object> utente;
    private HashMap<String,Object> prenotazione;

    //Elementi Grafici
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel loginPanel;
    private String pulsantiLogin[];
    private String pulsantiMenuPremium[];
    private String pulsantiMenuCliente[];
    private String pulsantiMenuAmministratore[];
    private JLabel menuLabel1;
    private JLabel menuLabel2;
    private JList<String> menuList;
    private JPanel menuPanel;

    public UiLogin(){
        usernameLabel = new JLabel("Username");
        passwordLabel = new JLabel("Password");
        usernameField = new JTextField("");
        usernameField.setToolTipText("Scrivere qui username");
        passwordField = new JPasswordField("");
        passwordField.setToolTipText("Scrivere qui password");
        passwordField.setEchoChar('*');

        loginPanel = new JPanel(new GridLayout(2,2));
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);

        pulsantiLogin = new String[2];
        pulsantiLogin[0] = "Crea Utente";
        pulsantiLogin[1] = "Login";

        pulsantiMenuPremium = new String[6];
        pulsantiMenuPremium[0] = "Occupa posto";
        pulsantiMenuPremium[1] = "Prenota posto";
        pulsantiMenuPremium[2] = "Modifica prenotazione";
        pulsantiMenuPremium[3] = "Richiedi ricarica";
        pulsantiMenuPremium[4] = "Interrompi ricarica";
        pulsantiMenuPremium[5] = "Modifica dati";

        pulsantiMenuCliente = new String[5];
        pulsantiMenuCliente[0] = "Occupa posto";
        pulsantiMenuCliente[1] = "Richiedi ricarica";
        pulsantiMenuCliente[2] = "Interrompi ricarica";
        pulsantiMenuCliente[3] = "Diventa premium";
        pulsantiMenuCliente[4] = "Modifica dati";

        pulsantiMenuAmministratore = new String[1];
        pulsantiMenuAmministratore[0] = "Monitora parcheggio";

        menuLabel1 = new JLabel();
        menuLabel2 = new JLabel("Seleziona servizio.(X per Logout)");
        menuList = new JList<String>();
        menuList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        menuList.setToolTipText("Seleziona un servizio e clicca OK");
        menuPanel = new JPanel(new BorderLayout(5,5));
        menuPanel.add(menuLabel1,BorderLayout.NORTH);
        menuPanel.add(menuLabel2,BorderLayout.CENTER);
        menuPanel.add(menuList,BorderLayout.SOUTH);

        uiRicarica = new UiRicarica();
        uiMonitora = new UiMonitora();
        uiUtente = new UiUtente();
        uiPosteggio = new UiPosteggio();

        utente = new HashMap<>();
        prenotazione = new HashMap<>();
    }

    public void avvioLogin(){
        do {
            this.mostraFormLogin();
            if (scelta == 0) {
                System.out.println("Creazione di un nuovo utente.");
                utente = uiUtente.avviaCreaUtente();
            }
            if (scelta == 1) {
                esitoRicerca = ricercaCredenziali(username, password);
                if (esitoRicerca.contains("errore"))
                    this.mostraErrore(esitoRicerca);
                else {
                    utente = ricercaUtente(username);
                    do {
                        this.mostraMenu((String) utente.get("nome"), (String) utente.get("tipo"));
                        String tipoUtente = (String) utente.get("tipo");
                        if (tipoUtente.equals("3")) {
                            if (sceltaMenu == 0) {
                                System.out.println("Monitoraggio del parcheggio da parte di un amministratore.");
                                uiMonitora.avviaMonitoraParcheggio();
                            }
                        } else if (tipoUtente.equals("1")) { // Utente Premium
                            switch (sceltaMenu) {
                                case 0:
                                    System.out.println("Occupazione di un posto.");
                                    prenotazione = uiPosteggio.avviaOccupaPosto(utente);
                                    break;
                                case 1:
                                    System.out.println("Prenotazione di un posto da parte di un utente premium.");
                                    prenotazione = uiPosteggio.avviaPrenotaPosto(utente);
                                    break;
                                case 2:
                                    System.out.println("Modifica della prenotazione da parte di un utente premium.");
                                    prenotazione = uiPosteggio.avviaModificaPrenotazione(utente,prenotazione);
                                    break;
                                case 3:
                                    System.out.println("Richiesta di estensione della ricarica.");
                                    uiRicarica.avviaRichiediRicarica();
                                    break;
                                case 4:
                                    System.out.println("Interruzione della ricarica.");
                                    uiRicarica.avviaInterrompiRicarica();
                                    break;
                                case 5:
                                    System.out.println("Modifica dei dati dell'utente.");
                                    utente = uiUtente.avviaModificaDati(utente);
                                    break;
                            }
                        } else if (tipoUtente.equals("2")) { // Cliente
                            switch (sceltaMenu) {
                                case 0:
                                    System.out.println("Occupazione di un posto.");
                                    prenotazione = uiPosteggio.avviaOccupaPosto(utente);
                                    break;
                                case 1:
                                    System.out.println("Richiesta di estensione della ricarica.");
                                    uiRicarica.avviaRichiediRicarica();
                                    break;
                                case 2:
                                    System.out.println("Interruzione della ricarica.");
                                    uiRicarica.avviaInterrompiRicarica();
                                    break;
                                case 3:
                                    System.out.println("Un cliente sta cercando di diventare premium.");
                                    utente = uiUtente.avviaDiventaPremium(utente);
                                    break;
                                case 4:
                                    System.out.println("Modifica dei dati dell'utente.");
                                    utente = uiUtente.avviaModificaDati(utente);
                                    break;
                            }
                        }
                    } while (sceltaMenu != -1);
                }
            }
        } while (scelta != -1);
    }

    private HashMap<String, Object> ricercaUtente(String username) {
        HttpURLConnection con = null;
        HashMap<String,Object> utente = new HashMap<>();

        try {
            URL url = new URL(baseURL + "/utenti/"+username);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            int responseCode = con.getResponseCode();
            if(responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Gson gson = new Gson();
                utente = gson.fromJson(response.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
                System.out.println("Utente trovato: " + utente.toString());
                return utente;
            }
            else{
                System.out.println("Errore nella ricerca dell'utente, codice di risposta: " + responseCode);
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        finally {
            if(con != null)
                con.disconnect();
        }
    }

    private String ricercaCredenziali(String username, String password) {
        if(username.isEmpty() && password.isEmpty())
            return "erroreCredenziali";
        else if(username.isEmpty())
            return "erroreUsername";
        else if(password.isEmpty())
            return "errorePassword";

        HttpURLConnection con = null;
        try {
            URL url = new URL(baseURL + "/credenziali/"+username+"/"+password);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            int responseCode = con.getResponseCode();
            if(responseCode == 200) {
                System.out.println("Credenziali corrette.");
                return "corretto";
            }
            else {
                System.out.println("Credenziali errate o utente non presente, codice di risposta: " + responseCode);
                return "erroreAssente";
            }

        }catch (Exception e){
            e.printStackTrace();
            return "erroreConnessione";
        }
        finally {
            if(con != null)
                con.disconnect();
        }
    }

    private void mostraMenu(String nome, String tipo) {
        int pulsante;
        String tipoCliente = "";

        usernameField.setText("");
        passwordField.setText("");
        usernameField.setBackground(Color.WHITE);
        passwordField.setBackground(Color.WHITE);

        menuLabel1.setText("Ciao\n " + nome);

        if (tipo.equals("1")) {
            tipoCliente = "Premium";
            menuList.setListData(pulsantiMenuPremium);
        }
        if (tipo.equals("2")) {
            tipoCliente = "Cliente";
            menuList.setListData(pulsantiMenuCliente);
        }
        if (tipo.equals("3")) {
            tipoCliente = "Amministratore";
            menuList.setListData(pulsantiMenuAmministratore);
        }
        menuList.setSelectedIndex(0);

        pulsante = showConfirmDialog(null, menuPanel, "Menu " + tipoCliente, DEFAULT_OPTION, QUESTION_MESSAGE, null);
        if (pulsante == OK_OPTION)
            sceltaMenu = menuList.getSelectedIndex();
        else
            sceltaMenu = -1;
    }

    private void mostraFormLogin() {
        scelta = showOptionDialog(null, loginPanel, "Login (clicca su X per uscire)", DEFAULT_OPTION, QUESTION_MESSAGE, null, pulsantiLogin, "Login");

        if (scelta == 0) // crea utente
        {
            usernameField.setText("");
            passwordField.setText("");
            usernameField.setBackground(Color.WHITE);
            passwordField.setBackground(Color.WHITE);
        }
        if (scelta == 1) // login
        {
            username = usernameField.getText();
            password = new String(passwordField.getPassword());
            System.out.println("\nTentativo di login con username: " + username);
            usernameField.setBackground(Color.WHITE);
            passwordField.setBackground(Color.WHITE);
        }
    }

    private void mostraErrore(String tipoErrore){
        String messaggio="";

        if (tipoErrore.equals("erroreUsername"))
        {
            messaggio="Username mancante.";
            System.out.println("Errore: " + messaggio);
        }
        if (tipoErrore.equals("errorePassword"))
        {
            messaggio="Password mancante.";
            System.out.println("Errore: " + messaggio);
        }
        if (tipoErrore.equals("erroreCredenziali"))
        {
            messaggio="Credenziali mancanti.";
            System.out.println("Errore: " + messaggio);
        }
        if (tipoErrore.equals("erroreAssente"))
        {
            messaggio="Credenziali errate.";
            usernameField.setBackground(Color.RED);
            passwordField.setBackground(Color.RED);
            System.out.println("Errore: " + messaggio);
        }
        if (tipoErrore.equals("erroreConnessione"))
        {
            messaggio="Server non raggiungibile.";
            System.out.println("Errore: " + messaggio);
        }

        messaggio = messaggio + "\n(clicca su OK o X per continuare)";

        showMessageDialog(null, messaggio, "Errore", ERROR_MESSAGE);
    }
}
