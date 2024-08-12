import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static javax.swing.JOptionPane.*;
import static javax.swing.JOptionPane.OK_OPTION;

public class UiRicarica {
    private int scelta;
    private int sceltaMenu;
    private String pulsantiScelta[];
    private JList<String> menuList;
    private JLabel menuLabel1;
    private JLabel menuLabel2;
    private JPanel menuPanel;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String baseURL = "http://localhost:4568/api/v1.0";

    public UiRicarica(){
        pulsantiScelta = new String[3];
        pulsantiScelta[0] = "Richiedi ricarica";
        pulsantiScelta[1] = "Richiedi estensione ricarica";
        pulsantiScelta[2] = "Interrompi ricarica";

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


    private void mostraEstensioneRicarica() {
        JTextField percentualeAttuale = new JTextField(10);
        JTextField nuovaPercentuale = new JTextField(10);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Percentuale di ricarica attuale"), gbc);
        gbc.gridx = 1;
        panel.add(percentualeAttuale, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Nuova percentuale di carica richiesta"), gbc);
        gbc.gridx = 1;
        panel.add(nuovaPercentuale, gbc);

        scelta = showConfirmDialog(null, panel, "Richiedi estensione ricarica", OK_CANCEL_OPTION, QUESTION_MESSAGE);

        if(scelta == OK_OPTION){
            /*
            String attuale = percentualeAttuale.getText();
            String nuovaPercentualeRichiesta = nuovaPercentuale.getText();

            Map<String, Object> estensione = new HashMap<>();
            estensione.put("percentuale_attuale", attuale);
            estensione.put("nuova_percentuale", nuovaPercentualeRichiesta);

            if(RestAPI_Adapter.put("/estendi_ricarica", estensione)) {
                mostraSuccesso("Ricarica estesa con successo");
            } else {
                mostraErrore("Errore nella richiesta di estensione ricarica");
            }

             */
        }
    }

    private void mostraInterrompiRicarica(Object id_prenotazione) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Interrompere ricarica in corso?"), gbc);

        scelta = showConfirmDialog(null, panel, "Interrompi ricarica", OK_CANCEL_OPTION, QUESTION_MESSAGE);

        if(scelta == OK_OPTION){
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = null;
            HttpResponse<String> response = null;
            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI(baseURL + "/ricariche?id_prenotazione="+id_prenotazione))
                        .DELETE()
                        .header("Content-Type", "application/json")
                        .build();
                System.out.println(request.uri());
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (URISyntaxException | IOException | InterruptedException e) {
                this.mostraErrore("Errore comunicazione server");
            }
            if(response == null){
                this.mostraErrore("Errore comunicazione server");
                return;
            }
            if(response.statusCode() == 200) this.mostraSuccesso("Ricarica interrotta");
            else if(response.statusCode() == 404 || response.statusCode() == 500) this.mostraErrore("Errore: ricarica non interrotta");
        }
    }

    private void mostraErrore(String testoErrore){
        showMessageDialog(null, testoErrore, "Errore", ERROR_MESSAGE);
    }

    private void mostraSuccesso(String testoSuccesso){
        showMessageDialog(null, testoSuccesso, "Successo", INFORMATION_MESSAGE);
    }

    public void avviaInterrompiRicarica(HashMap<String,Object> utente) {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/statoUtente?user="+utente.get("username")))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            HashMap<String, Object> statoUtente = gson.fromJson(response.body(), type);

            System.out.println(statoUtente.get("caricando"));
            if(statoUtente.get("caricando").toString().equals("si")) this.mostraInterrompiRicarica(statoUtente.get("id_prenotazione"));
            else this.mostraErrore("Nessuna ricarica in corso");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            this.mostraErrore("Errore comunicazione server");
        }
    }

    public void avviaRichiediRicarica(HashMap<String,Object> utente) {
        //check possibilità di ricaricare
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/statoUtente?user="+utente.get("username")))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            HashMap<String, Object> statoUtente = gson.fromJson(response.body(), type);

            System.out.println(statoUtente.get("caricando"));
            if(statoUtente.get("occupazione_iniziata").toString().equals("no")) this.mostraErrore("La prenotazione non e' iniziata\nParcheggiare il veicolo e ritentare");
            else if(statoUtente.get("caricando").toString().equals("si")) this.mostraErrore("Ricarica gia' richiesta");
            else this.mostraFormRicarica(utente);;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            this.mostraErrore("Errore comunicazione server");
        }
    }

    private void mostraFormRicarica(HashMap<String,Object> utente) {
        HttpRequest request = null;
        HttpResponse response = null;
        HttpClient client = HttpClient.newHttpClient();
        // Create a combo box with numbers from 1 to 100
        Integer[] numbers = new Integer[100];
        for (int i = 0; i < 100; i++) {
            numbers[i] = i + 1;
        }
        JComboBox<Integer> numberComboBox = new JComboBox<>(numbers);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Percentuale da ricaricare"), gbc);
        gbc.gridx = 1;
        panel.add(numberComboBox, gbc);

        scelta = showConfirmDialog(null, panel, "Richiedi estensione ricarica", OK_CANCEL_OPTION, QUESTION_MESSAGE);

        if(scelta == OK_OPTION){
            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI(baseURL + "/ricariche?user="+utente.get("username")+"&charge_time="+numberComboBox.getSelectedItem()))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .header("Content-Type", "application/json")
                        .build();
                System.out.println(request.uri());
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if(response.statusCode() == 200) this.mostraSuccesso("Ricarica richiesta con successo");
                else this.mostraErrore("Richiesta ricarica non completata");
            } catch (URISyntaxException | IOException | InterruptedException e) {
                this.mostraErrore("Errore comunicazione server" + e);
            }
        }
    }
}
