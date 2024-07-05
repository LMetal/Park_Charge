import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Prenotazioni {
    private int id;
    private String tempo_arrivo;
    private String tempo_uscita;
    private String utente;
    private int posto;
    private static final transient DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public Prenotazioni(int id, LocalDateTime tempo_arrivo, LocalDateTime tempo_uscita, String utente, int posto){
        this.id = id;
        this.tempo_arrivo = tempo_arrivo.format(formatter);
        this.tempo_uscita = tempo_uscita.format(formatter);
        this.utente = utente;
        this.posto = posto;
    }

    public Prenotazioni(int id, String tempo_arrivo, String tempo_uscita, String utente, int posto){
        this(id, LocalDateTime.parse(tempo_arrivo, formatter), LocalDateTime.parse(tempo_uscita, formatter), utente, posto);
    }

    public Prenotazioni(HashMap<String, Object> prenotazioneJson) {
        this.id = (Integer) prenotazioneJson.get("id");
        this.tempo_arrivo = (String) prenotazioneJson.get("tempo_arrivo");
        this.tempo_uscita = (String) prenotazioneJson.get("tempo_uscita");
        this.utente = (String) prenotazioneJson.get("utente");
        this.posto = (Integer) prenotazioneJson.get("posto");
    }

    public Prenotazioni(){
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getTempo_arrivo() {
        return LocalDateTime.parse(tempo_arrivo, formatter);
    }

    public LocalDateTime getTempo_uscita() {
        return LocalDateTime.parse(tempo_uscita, formatter);
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

    public void setTempo_arrivo(String tempo_arrivo) {
        this.tempo_arrivo = tempo_arrivo;
    }

    public void setTempo_uscita(String tempo_uscita) {
        this.tempo_uscita = tempo_uscita;
    }

    public void setUtente(String utente) {
        this.utente = utente;
    }

    public void setId(int id) {
        this.id = id;
    }
}
