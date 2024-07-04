import DataBase.DbUtenti;

public class GestoreUtenti {
    private DbUtenti dbUtenti;

    // Costruttore
    public GestoreUtenti(){
        this.dbUtenti = new DbUtenti();
    }

    // Metodo per ottenere le credenziali dell'utente dato username e password
    public Credenziali getCredenziali(String username, String password) {
        String comandoSql = "SELECT * FROM Credenziali WHERE username=\"" +username+ "\"AND password=\"" +password+ "\"";
        System.out.println(comandoSql);
        var rs = dbUtenti.query(comandoSql);

        // Se non ci sono risultati, restituisce null
        if(rs.isEmpty()) return null;

        // Crea un oggetto Credenziali con i dati ottenuti dal risultato della query
        Credenziali credenziali = new Credenziali(rs.get(0).get("username").toString(),rs.get(0).get("password").toString());

        return credenziali;
    }

    // Metodo per ottenere le informazioni dell'utente dato username
    public Utente getUtente(String username) {
        String comandoSql = "SELECT * FROM Utente WHERE username=\"" +username+ "\"";
        System.out.println(comandoSql);
        var rs = dbUtenti.query(comandoSql);

        // Se non ci sono risultati, restituisce null
        if(rs.isEmpty()) return null;

        // Crea un oggetto Utente con i dati ottenuti dal risultato della query
        Utente utente = new Utente(rs.get(0).get("nome").toString(),rs.get(0).get("cognome").toString(),rs.get(0).get("username").toString(), (Integer) rs.get(0).get("tipo"),rs.get(0).get("carta").toString());

        return utente;
    }

    // Metodo per creare un nuovo utente e le relative credenziali
    public String creaUtenti(Utente utente, Credenziali credenziali) {
        // Query per controllare se l'username è già presente nella tabella Utente
        String controlloSql = "SELECT * FROM Utente WHERE username=\"" + utente.getUsername() + "\"";
        // Query per inserire le credenziali nella tabella Credenziali
        String credenzialiSql = "INSERT INTO Credenziali (username,password) VALUES (\"" + credenziali.getUsername() + "\",\"" + credenziali.getPassword() + "\")";
        // Query per inserire l'utente nella tabella Utente
        String utenteSql = "INSERT INTO Utente (nome,cognome,username,tipo,carta) VALUES (\"" + utente.getNome() + "\",\"" + utente.getCognome() + "\",\"" + utente.getUsername() + "\",\"" + utente.getTipo() + "\",\"" + utente.getCarta() + "\")";

        System.out.println(controlloSql);
        var rs = dbUtenti.query(controlloSql);

        // Se l'username esiste già, restituisce un messaggio di errore
        if(!rs.isEmpty())
            return "Username gia esistente";

        System.out.println(credenzialiSql);
        boolean credenzialiBool = dbUtenti.update(credenzialiSql);

        // Se l'inserimento delle credenziali fallisce, restituisce un messaggio di errore
        if(!credenzialiBool)
            return "Errore nella creazione delle credenziali";

        System.out.println(utenteSql);
        boolean utenteBool = dbUtenti.update(utenteSql);

        // Se l'inserimento dell'utente fallisce, restituisce un messaggio di errore
        if(!utenteBool)
            return "Errore nella creazione delle utente";

        return "Successo";
    }

    // Metodo per modificare i dati di un utente dato username
    public boolean modificaDatiUtente(String username, Utente utente) {
        StringBuilder comandoSql = new StringBuilder("UPDATE Utente SET ");
        boolean primoCampo = true;

        // Aggiunge i campi da modificare alla query di aggiornamento
        if(utente.getNome() != null){
            comandoSql.append("nome=\"").append(utente.getNome()).append("\"");
            primoCampo = false;
        }
        if(utente.getCognome() != null){
            if(!primoCampo)
                comandoSql.append(",");
            comandoSql.append("cognome=\"").append(utente.getCognome()).append("\"");
            primoCampo = false;
        }
        if(utente.getCarta() != null){
            if(!primoCampo)
                comandoSql.append(",");
            comandoSql.append("carta=\"").append(utente.getCarta()).append("\"");
        }

        // Aggiunge la condizione WHERE per specificare l'utente da modificare
        comandoSql.append("WHERE username=\"" +username+ "\"");
        System.out.println(comandoSql);

        // Esegue la query di aggiornamento e restituisce il risultato
        return dbUtenti.update(comandoSql.toString());
    }

    // Metodo per cambiare il tipo di utente a premium dato username
    public boolean diventaPremium(String username) {
        String comandoSql = "UPDATE Utente SET tipo = 1 WHERE username=\"" +username+ "\"";
        System.out.println(comandoSql);

        // Esegue la query di aggiornamento e restituisce il risultato
        return dbUtenti.update(comandoSql);
    }
}
