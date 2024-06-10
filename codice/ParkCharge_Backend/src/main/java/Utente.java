public class Utente {
    private String nome;
    private String cognome;
    private String username;
    private int tipo;
    private String carta;

    public Utente(String nome, String cognome, String username, int tipo, String carta) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.tipo = tipo;
        this.carta = carta;
    }

    public String getNome() {
        return this.nome;
    }

    public String getCognome() {
        return this.cognome;
    }

    public String getUsername() {
        return this.username;
    }

    public int getTipo() {
        return this.tipo;
    }

    public String getCarta() {
        return this.carta;
    }


}
