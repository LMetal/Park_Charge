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

        Credenziali credenziali = new Credenziali(rs.get(0).get("username").toString(),rs.get(0).get("password").toString());

        return credenziali;
    }

    public Utente getUtente(String username) {
        String comandoSql = "SELECT * FROM Utente WHERE username=\"" +username+ "\"";
        System.out.println(comandoSql);
        var rs = dbUtenti.query(comandoSql);

        Utente utente = new Utente(rs.get(0).get("nome").toString(),rs.get(0).get("cognome").toString(),rs.get(0).get("username").toString(), (Integer) rs.get(0).get("tipo"),rs.get(0).get("carta").toString());

        return utente;
    }
}
