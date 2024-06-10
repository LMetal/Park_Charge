public class Ricariche {
    private float kilowatt;
    private int durata_ricarica;
    private int percentuale_richiesta;
    private int prenotazione;
    private int mwbot;

    public Ricariche(float kilowatt, int durata_ricarica, int percentuale_richiesta, int prenotazione, int mwbot){
        this.kilowatt = kilowatt;
        this.durata_ricarica = durata_ricarica;
        this.percentuale_richiesta = percentuale_richiesta;
        this.prenotazione = prenotazione;
        this.mwbot = mwbot;
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

    public int getPrenotazione() {
        return prenotazione;
    }

    public int getMwbot() {
        return mwbot;
    }
}
