import java.util.HashMap;

public class Ricariche {
    private float kilowatt;
    private int durata_ricarica;
    private int percentuale_richiesta;
    private int percentuale_erogata;
    private int prenotazione;
    private int mwbot;

    public Ricariche(float kilowatt, int durata_ricarica, int percentuale_richiesta, int prenotazione, int mwbot){
        this.kilowatt = kilowatt;
        this.durata_ricarica = durata_ricarica;
        this.percentuale_richiesta = percentuale_richiesta;
        this.percentuale_erogata = 0;
        this.prenotazione = prenotazione;
        this.mwbot = mwbot;
    }

    public Ricariche(HashMap<String, Object> ricaricaJson) {
        this.percentuale_richiesta = (int) ricaricaJson.get("percentuale_richiesta");
        this.prenotazione = (int) ricaricaJson.get("prenotazione");
        try{
            this.percentuale_erogata = (int) ricaricaJson.get("percentuale_erogata");
        } catch (Exception e){
            this.percentuale_erogata = 0;
        }

        this.mwbot = (int) ricaricaJson.get("MWBot");
    }

    public float getKilowatt() {
        return kilowatt;
    }

    public int getDurata_ricarica() {
        return durata_ricarica;
    }

    public int getPercentuale_richiesta() {
        return percentuale_richiesta;
    }

    public int getPercentuale_erogata(){return percentuale_erogata;}

    //TODO quanto l'MWBot fa publish dello stato aggiorno questo valore
    public void setPercentuale_erogata(int percentuale_erogata) {
        this.percentuale_erogata = percentuale_erogata;
    }

    public int getPrenotazione() {
        return prenotazione;
    }

    public int getMwbot() {
        return mwbot;
    }
}
