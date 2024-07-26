import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static javax.swing.JOptionPane.*;

public class UiMonitora {
    private int scelta;
    private int sceltaMenu;
    private String pulsantiScelta[];
    private JList<String> menuList;
    private JLabel menuLabel1;
    private JLabel menuLabel2;
    private JPanel menuPanel;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String baseURL = "http://localhost:4568/api/v1.0";

    private static Type type;
    private static Gson gson;

    public UiMonitora(){
        type = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
        gson = new Gson();

        pulsantiScelta = new String[5];
        pulsantiScelta[0] = "Aggiorna prezzi";
        pulsantiScelta[1] = "Visualizza stato posti";
        pulsantiScelta[2] = "Visualizza storico";
        pulsantiScelta[3] = "Visualizza ricariche in corso";
        pulsantiScelta[4] = "Visualizza prenotazioni";

        menuList = new JList<String>();
        menuList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        menuList.setToolTipText("Seleziona un servizio e clicca OK");

        menuLabel1 = new JLabel();
        menuLabel2 = new JLabel("Seleziona servizio.(X per Logout)");
        menuList = new JList<String>();
        menuList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        menuList.setToolTipText("Seleziona un servizio e clicca OK");
        menuPanel = new JPanel(new BorderLayout(5,5));
        menuPanel.add(menuLabel1, BorderLayout.NORTH);
        menuPanel.add(menuLabel2,BorderLayout.CENTER);
        menuPanel.add(menuList,BorderLayout.SOUTH);
    }
    public void avviaMonitoraParcheggio() {
        menuList.setListData(pulsantiScelta);

        do{
            scelta = showConfirmDialog(null, menuPanel, "Menu monitora parcheggio", DEFAULT_OPTION, QUESTION_MESSAGE, null);
            if (scelta == OK_OPTION){
                sceltaMenu = menuList.getSelectedIndex();
                if(sceltaMenu == 0)
                    this.mostraModificaPrezzi();
                if(sceltaMenu == 1)
                    this.mostraStatoPosti();
                if(sceltaMenu == 2)
                    this.mostraStorico();
                if(sceltaMenu == 3)
                    this.mostraRicariche();
                if(sceltaMenu == 4)
                    this.mostraPrenotazioni();
            }
            else
                sceltaMenu = -1;


        } while (sceltaMenu != -1);

    }

    private void mostraStorico() {
        String inputYear = showInputDialog(null, "Inserisci l'anno (yyyy):", "Filtro Storico", QUESTION_MESSAGE);
        String inputMonth = showInputDialog(null, "Inserisci il mese (MM):", "Filtro Storico", QUESTION_MESSAGE);

        int year;
        int month;
        try {
            year = Integer.parseInt(inputYear);
            month = Integer.parseInt(inputMonth);
            if (month < 1 || month > 12) {
                throw new NumberFormatException();
            }

            //parcheggio aperto nel 2020
            if (year < 2020 || year > LocalDateTime.now().getYear()) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            this.mostraErrore("Formato anno o mese non valido. Usa yyyy per l'anno e MM per il mese");
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = null;
        HttpResponse<String> response = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/storico?year="+year+"&month="+month))
                    .header("Content-Type", "application/json")
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            this.mostraErrore("Errore comunicazione server");
        }
        ArrayList<HashMap<String, Object>> storicoFiltrato = gson.fromJson(response.body(), type);

        if(storicoFiltrato.isEmpty()){
            this.mostraErrore("Nessun dato trovato");
            return;
        }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel headerUtente = new JLabel("Utente");
        JLabel headerArrivo = new JLabel("Tempo di Arrivo");
        JLabel headerUscita = new JLabel("Tempo di Uscita");
        JLabel headerPosto = new JLabel("Posto");
        JLabel headerPosteggio = new JLabel("Costo posteggio");
        JLabel headerRicarica = new JLabel("Costo ricarica");
        JLabel headerPenale = new JLabel("Costo penale");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(headerUtente, gbc);
        gbc.gridx = 1;
        panel.add(headerArrivo, gbc);
        gbc.gridx = 2;
        panel.add(headerUscita, gbc);
        gbc.gridx = 3;
        panel.add(headerPosto, gbc);
        gbc.gridx = 4;
        panel.add(headerPosteggio, gbc);
        gbc.gridx = 5;
        panel.add(headerRicarica, gbc);
        gbc.gridx = 6;
        panel.add(headerPenale, gbc);

        int row = 1;

        for (HashMap<String, Object> prenotazione: storicoFiltrato) {
            JLabel labelUtente = new JLabel(prenotazione.get("utente").toString());
            JLabel labelArrivo = new JLabel(prenotazione.get("tempo_arrivo").toString());
            JLabel labelUscita = new JLabel(prenotazione.get("tempo_uscita").toString());
            JLabel labelPosto = new JLabel(prenotazione.get("posto").toString());
            JLabel labelPosteggio = new JLabel(prenotazione.get("costo_posteggio").toString());
            JLabel labelRicarica = new JLabel(prenotazione.get("costo_ricarica").toString());
            JLabel labelPenale = new JLabel(prenotazione.get("penale").toString());

            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(labelUtente, gbc);
            gbc.gridx = 1;
            panel.add(labelArrivo, gbc);
            gbc.gridx = 2;
            panel.add(labelUscita, gbc);
            gbc.gridx = 3;
            panel.add(labelPosto, gbc);
            gbc.gridx = 4;
            panel.add(labelPosteggio, gbc);
            gbc.gridx = 5;
            panel.add(labelRicarica, gbc);
            gbc.gridx = 6;
            panel.add(labelPenale, gbc);

            row++;
        }

        showConfirmDialog(null, panel, "Lista storico", DEFAULT_OPTION, QUESTION_MESSAGE, null);
    }

    private void mostraRicariche() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = null;
        HttpResponse<String> response = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/ricariche"))
                    .header("Content-Type", "application/json")
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            this.mostraErrore("Errore comunicazione server");
        }
        ArrayList<HashMap<String, Object>> listaRicariche = gson.fromJson(response.body(), type);


        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel headerPrenotazione = new JLabel("Prenotazione");
        JLabel headerKilowatt = new JLabel("Kilowatt utilizzati");
        JLabel headerDurata = new JLabel("Durata della ricarica");
        JLabel headerPercentuale = new JLabel("Percentuale richiesta");
        JLabel headerBot = new JLabel("MWBot");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(headerPrenotazione, gbc);
        gbc.gridx = 1;
        panel.add(headerKilowatt, gbc);
        gbc.gridx = 2;
        panel.add(headerDurata, gbc);
        gbc.gridx = 3;
        panel.add(headerPercentuale, gbc);
        gbc.gridx = 4;
        panel.add(headerBot, gbc);

        int row = 1;
        for (HashMap<String, Object> ricarica : listaRicariche) {
            JLabel labelId = new JLabel(ricarica.get("prenotazione").toString());
            JLabel labelKilowatt = new JLabel(ricarica.get("kilowatt").toString());
            JLabel labelDurata = new JLabel(ricarica.get("durata_ricarica").toString());
            JLabel labelPercentuale = new JLabel(ricarica.get("percentuale_richiesta").toString());
            JLabel labelBot = new JLabel(ricarica.get("mwbot").toString());

            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(labelId, gbc);
            gbc.gridx = 1;
            panel.add(labelKilowatt, gbc);
            gbc.gridx = 2;
            panel.add(labelDurata, gbc);
            gbc.gridx = 3;
            panel.add(labelPercentuale, gbc);
            gbc.gridx = 4;
            panel.add(labelBot, gbc);

            row++;
        }

        showConfirmDialog(null, panel, "Lista ricariche", DEFAULT_OPTION, QUESTION_MESSAGE, null);

    }

    private void mostraStatoPosti() {
        var listaPosti = RestAPI_Adapter.get("/posti");
        System.out.println(listaPosti);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel headerPosto = new JLabel("Posto");
        JLabel headerStato = new JLabel("Stato");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(headerPosto, gbc);
        gbc.gridx = 1;
        panel.add(headerStato, gbc);

        int row = 1;
        for (HashMap<String, Object> posto : listaPosti) {
            JLabel labelNumeroPosto = new JLabel(posto.get("id").toString());
            JLabel labelStato;
            if(posto.get("disponibilita").toString().equals("1.0")){
                labelStato = new JLabel("OCCUPATO");
            } else {
                labelStato = new JLabel("LIBERO");
            }


            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(labelNumeroPosto, gbc);
            gbc.gridx = 1;
            panel.add(labelStato, gbc);

            row++;
        }
        showConfirmDialog(null, panel, "Stato posti", DEFAULT_OPTION, QUESTION_MESSAGE, null);
    }

    private void mostraPrenotazioni() {
        var listaPrenotazioni = RestAPI_Adapter.get("/prenotazioni");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel headerId = new JLabel("ID");
        JLabel headerArrivo = new JLabel("Tempo di Arrivo");
        JLabel headerUscita = new JLabel("Tempo di Uscita");
        JLabel headerUtente = new JLabel("Utente");
        JLabel headerPosto = new JLabel("Posto");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(headerId, gbc);
        gbc.gridx = 1;
        panel.add(headerArrivo, gbc);
        gbc.gridx = 2;
        panel.add(headerUscita, gbc);
        gbc.gridx = 3;
        panel.add(headerUtente, gbc);
        gbc.gridx = 4;
        panel.add(headerPosto, gbc);

        int row = 1;

        for (HashMap<String, Object> prenotazione: listaPrenotazioni) {
            JLabel labelId = new JLabel(prenotazione.get("id").toString());
            JLabel labelArrivo = new JLabel(prenotazione.get("tempo_arrivo").toString());
            JLabel labelUscita = new JLabel(prenotazione.get("tempo_uscita").toString());
            JLabel labelUtente = new JLabel(prenotazione.get("utente").toString());
            JLabel labelPosto = new JLabel(prenotazione.get("posto").toString());

            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(labelId, gbc);
            gbc.gridx = 1;
            panel.add(labelArrivo, gbc);
            gbc.gridx = 2;
            panel.add(labelUscita, gbc);
            gbc.gridx = 3;
            panel.add(labelUtente, gbc);
            gbc.gridx = 4;
            panel.add(labelPosto, gbc);

            row++;
        }

        showConfirmDialog(null, panel, "Lista prenotazioni", DEFAULT_OPTION, QUESTION_MESSAGE, null);

    }

    private void mostraModificaPrezzi() {
        var costiAttuali = RestAPI_Adapter.get("/costo").get(0);

        JFrame frame = new JFrame("Modifica prezzi Parcheggio (X per uscire)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel labelPosteggio = new JLabel("Costo posteggio");
        JLabel labelRicarica = new JLabel("Costo ricarica");
        JLabel labelPenale = new JLabel("Prezzo penale");
        JLabel labelPremium = new JLabel("Costo premium");

        JTextField textPosteggio = new JTextField(10);
        JTextField textRicarica = new JTextField(10);
        JTextField textPenale = new JTextField(10);
        JTextField textPremium = new JTextField(10);

        JLabel currentPosteggio = new JLabel(costiAttuali.get("costo_posteggio").toString() + " euro/ora");
        JLabel currentRicarica = new JLabel(costiAttuali.get("costo_ricarica").toString() + " euro/KW");
        JLabel currentPenale = new JLabel(costiAttuali.get("penale").toString() + " euro");
        JLabel currentPremium = new JLabel(costiAttuali.get("costo_premium").toString() + " euro");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(labelPosteggio, gbc);
        gbc.gridx = 1;
        panel.add(textPosteggio, gbc);
        gbc.gridx = 2;
        panel.add(currentPosteggio, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(labelRicarica, gbc);
        gbc.gridx = 1;
        panel.add(textRicarica, gbc);
        gbc.gridx = 2;
        panel.add(currentRicarica, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(labelPenale, gbc);
        gbc.gridx = 1;
        panel.add(textPenale, gbc);
        gbc.gridx = 2;
        panel.add(currentPenale, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(labelPremium, gbc);
        gbc.gridx = 1;
        panel.add(textPremium, gbc);
        gbc.gridx = 2;
        panel.add(currentPremium, gbc);


        scelta = showConfirmDialog(null, panel, "Modifica prezzi", DEFAULT_OPTION, QUESTION_MESSAGE, null);

        if(scelta == OK_OPTION){
            //verifica dati
            System.out.println(textPosteggio.getText());
            try{
                if(Integer.parseInt(textPosteggio.getText()) < 0){
                    this.mostraErrore("Costo posteggio minore di zero");
                    return;
                }
                if(Integer.parseInt(textRicarica.getText()) < 0){
                    this.mostraErrore("Costo ricarica minore di zero");
                    return;
                }
                if(Integer.parseInt(textPenale.getText()) < 0){
                    this.mostraErrore("Costo penale minore di zero");
                    return;
                }
                if(Integer.parseInt(textPremium.getText()) < 0){
                    this.mostraErrore("Costo premium minore di zero");
                    return;
                }
            } catch (Exception e){
                this.mostraErrore("Errore inserimento dati");
                return;
            }

            Map<String, Object> costi = new HashMap<>();
            costi.put("costo_posteggio", textPosteggio.getText());
            costi.put("costo_ricarica", textPosteggio.getText());
            costi.put("penale", textPosteggio.getText());
            costi.put("costo_premium", textPosteggio.getText());

            if(RestAPI_Adapter.put("/costo", costi)) {
                this.mostraSuccesso("Costi modificati con successo");
            }else{
                this.mostraErrore("Costi non modificati, errore di connessione al Backend");
            }
        }
    }

    private void mostraErrore(String testoErrore){
        showMessageDialog(null, testoErrore, "Errore", ERROR_MESSAGE);
    }

    private void mostraSuccesso(String testoSuccesso){
        showMessageDialog(null, testoSuccesso, "Successo", INFORMATION_MESSAGE);
    }
}


