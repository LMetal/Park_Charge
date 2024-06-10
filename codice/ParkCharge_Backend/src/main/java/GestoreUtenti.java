import DataBase.DbUtenti;

public class GestoreUtenti {
    private DbUtenti dbUtenti;

    public GestoreUtenti(){
        this.dbUtenti = new DbUtenti();
    }

    public Credenziali getCredenziali(String username, String password) {
        String comandoSql = "SELECT * FROM Credenziali WHERE username=\"" +username+ "\"AND password=\"" +password+ "\"";
        System.out.println(comandoSql);
        var rs = dbUtenti.query(comandoSql);

        Credenziali credenziali = new Credenziali(rs.getFirst().get("username").toString(),rs.getFirst().get("password").toString());

        return credenziali;
    }

    public Utente getUtente(String username) {
        String comandoSql = "SELECT * FROM Utente WHERE username=\"" +username+ "\"";
        System.out.println(comandoSql);
        var rs = dbUtenti.query(comandoSql);

        Utente utente = new Utente(rs.getFirst().get("nome").toString(),rs.getFirst().get("cognome").toString(),rs.getFirst().get("username").toString(), (Integer) rs.getFirst().get("tipo"),rs.getFirst().get("carta").toString());

        return utente;
    }
}
