import DataBase.DbPrenotazioni;
import DataBase.DbRicariche;
import DataBase.DbUtenti;

public class GestoreRicariche {
    private DbPrenotazioni dbPrenotazioni;
    private DbRicariche dbRicariche;

    public GestoreRicariche(){
        this.dbPrenotazioni = new DbPrenotazioni();
        this.dbRicariche = new DbRicariche();
    }
}
