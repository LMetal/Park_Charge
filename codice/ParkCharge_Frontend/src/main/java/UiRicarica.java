import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
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

    public void avviaGestioneRicarica() {
        menuList.setListData(pulsantiScelta);

        do{
            scelta = showConfirmDialog(null, menuPanel, "Menu gestione ricarica", DEFAULT_OPTION, QUESTION_MESSAGE, null);
            if (scelta == OK_OPTION){
                sceltaMenu = menuList.getSelectedIndex();
                if(sceltaMenu == 0)
                    this.mostraFormRicarica();
                if(sceltaMenu == 1)
                    this.mostraEstensioneRicarica();
                if(sceltaMenu == 2)
                    this.mostraInterrompiRicarica();
            }
            else
                sceltaMenu = -1;

        } while (sceltaMenu != -1);
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
        }
    }

    private void mostraInterrompiRicarica() {
        JTextField interrompiRicarica = new JTextField(10);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Interrompi"), gbc);
        gbc.gridx = 1;
        panel.add(interrompiRicarica, gbc);

        scelta = showConfirmDialog(null, panel, "Interrompi ricarica", OK_CANCEL_OPTION, QUESTION_MESSAGE);

        if(scelta == OK_OPTION){
            String nuovaPercentuale = interrompiRicarica.getText();

            Map<String, Object> interruzione = new HashMap<>();
            interruzione.put("interrompiRicarica", nuovaPercentuale);

            if(RestAPI_Adapter.put("/interrompi_ricarica", interruzione)) {
                mostraSuccesso("Ricarica interrotta con successo");
            } else {
                mostraErrore("Errore nell'interruzione della ricarica");
            }
        }
    }

    private void mostraErrore(String testoErrore){
        showMessageDialog(null, testoErrore, "Errore", ERROR_MESSAGE);
    }

    private void mostraSuccesso(String testoSuccesso){
        showMessageDialog(null, testoSuccesso, "Successo", INFORMATION_MESSAGE);
    }

    public void avviaInterrompiRicarica() {
    }

    public void avviaRichiediRicarica() {
        this.mostraFormRicarica();
    }

    private void mostraFormRicarica() {

    }
}
