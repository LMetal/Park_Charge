import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Prenotazioni {
    private int id;
    private LocalDateTime tempo_arrivo;
    private LocalDateTime tempo_uscita;
    private String utente;
    private int posto;

    public Prenotazioni(int id, LocalDateTime tempo_arrivo, LocalDateTime tempo_uscita, String utente, int posto){
        this.id = id;
        this.tempo_arrivo = tempo_arrivo;
        this.tempo_uscita = tempo_uscita;
        this.utente = utente;
        this.posto = posto;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getTempo_arrivo() {
        return tempo_arrivo;
    }

    public LocalDateTime getTempo_uscita() {
        return tempo_uscita;
    }

    public String getUtente() {
        return utente;
    }

    public int getPosto() {
        return posto;
    }
}
