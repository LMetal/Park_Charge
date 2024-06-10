public class Costi {
    private float costo_posteggio;
    private float costo_ricarica;
    private int penale;
    private int costo_premium;

    public Costi(float costo_posteggio, float costo_ricarica, int penale, int costo_premium){
        this.costo_posteggio = costo_posteggio;
        this.costo_ricarica = costo_ricarica;
        this.penale = penale;
        this.costo_premium = costo_premium;
    }

    public float getCosto_posteggio() {
        return costo_posteggio;
    }

    public float getCosto_ricarica() {
        return costo_ricarica;
    }

    public int getPenale() {
        return penale;
    }

    public int getCosto_premium() {
        return costo_premium;
    }
}
