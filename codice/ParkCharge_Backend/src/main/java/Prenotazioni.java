import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Prenotazioni {
    private int id;
    private String tempo_arrivo;
    private String tempo_uscita;
    private String utente;
    private int posto;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Prenotazioni(int id, LocalDateTime tempo_arrivo, LocalDateTime tempo_uscita, String utente, int posto){
        this.id = id;
        this.tempo_arrivo = tempo_arrivo.format(formatter);
        this.tempo_uscita = tempo_uscita.format(formatter);
        this.utente = utente;
        this.posto = posto;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getTempo_arrivo() {
        return LocalDateTime.parse(tempo_arrivo,formatter);
    }

    public LocalDateTime getTempo_uscita() {
        return LocalDateTime.parse(tempo_uscita,formatter);
    }

    public String getUtente() {
        return utente;
    }

    public int getPosto() {
        return posto;
    }

    public void setPosto(int posto) {
        this.posto = posto;
    }
}
