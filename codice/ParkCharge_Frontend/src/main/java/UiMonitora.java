import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static javax.swing.JOptionPane.*;

public class UiMonitora {
    private String baseURL = "http://localhost:4568/api/v1.0";
    private int scelta;
    private int sceltaMenu;
    private JPanel monitoraMenu;
    private String pulsantiScelta[];
    private JList<String> menuList;
    private JLabel menuLabel1;
    private JLabel menuLabel2;
    private JPanel menuPanel;

    public UiMonitora(){
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
                    this.mostraPrenotazioni();
            }
            else
                sceltaMenu = -1;


        } while (sceltaMenu != -1);

    }

    private void mostraPrenotazioni() {
        var listaPrenotazioni = this.getPrenotazioni();

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

        for (HashMap<String, Object> prenotazione : listaPrenotazioni) {
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

        scelta = showConfirmDialog(null, panel, "Lista prenotazioni", DEFAULT_OPTION, QUESTION_MESSAGE, null);

    }



    private void mostraModificaPrezzi() {
        var costiAttuali = this.getPrezzi();

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

        JLabel currentPosteggio = new JLabel(costiAttuali.get("costo_posteggio").toString());
        JLabel currentRicarica = new JLabel(costiAttuali.get("costo_ricarica").toString());
        JLabel currentPenale = new JLabel(costiAttuali.get("penale").toString());
        JLabel currentPremium = new JLabel(costiAttuali.get("costo_premium").toString());

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
            System.out.println("PUT dati");
        }
    }

    private HashMap<String, Object> getPrezzi() {
        HttpURLConnection con = null;
        HashMap<String,Object> costi;

        try {
            URL url = new URL(baseURL + "/costi");
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
                costi = gson.fromJson(response.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
                //return utente;
                System.out.println(costi);
                return costi;
            }
            else
                return null;

        }catch (Exception e){
            e.printStackTrace();
            //return null;
        }
        finally {
            if(con != null)
                con.disconnect();
        }
        return null;
    }

    private ArrayList<HashMap<String,Object>> getPrenotazioni() {
        HttpURLConnection con = null;
        ArrayList<HashMap<String,Object>> prenotazioni;

        try {
            URL url = new URL(baseURL + "/prenotazioni");
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
                //prenotazioni = gson.fromJson(response.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
                //return utente;

                Type type = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
                prenotazioni = gson.fromJson(String.valueOf(response), type);
                System.out.println(prenotazioni);
                return prenotazioni;
            }
            else
                return null;

        }catch (Exception e){
            e.printStackTrace();
            //return null;
        }
        finally {
            if(con != null)
                con.disconnect();
        }
        return null;
    }
}


