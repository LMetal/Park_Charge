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

        if(rs.isEmpty()) return null;

        Credenziali credenziali = new Credenziali(rs.get(0).get("username").toString(),rs.get(0).get("password").toString());

        return credenziali;
    }

    public Utente getUtente(String username) {
        String comandoSql = "SELECT * FROM Utente WHERE username=\"" +username+ "\"";
        System.out.println(comandoSql);
        var rs = dbUtenti.query(comandoSql);

        if(rs.isEmpty()) return null;

        Utente utente = new Utente(rs.get(0).get("nome").toString(),rs.get(0).get("cognome").toString(),rs.get(0).get("username").toString(), (Integer) rs.get(0).get("tipo"),rs.get(0).get("carta").toString());

        return utente;
    }

    public String creaUtenti(Utente utente, Credenziali credenziali) {
        String controlloSql = "SELECT * FROM Utente WHERE username=\"" +utente.getUsername()+ "\"";
        String credenzialiSql = "INSERT INTO Credenziali (username,password) VALUES (\"" + credenziali.getUsername() + "\",\"" + credenziali.getPassword() + "\")";
        String utenteSql = "INSERT INTO Utente (nome,cognome,username,tipo,carta) VALUES (\"" + utente.getNome() + "\",\"" + utente.getCognome() + "\",\"" + utente.getUsername() + "\",\"" + utente.getTipo() + "\",\"" + utente.getCarta() + "\")";

        System.out.println(controlloSql);
        var rs = dbUtenti.query(controlloSql);
        if(!rs.isEmpty())
            return "Username gia esistente";

        System.out.println(credenzialiSql);
        boolean credenzialiBool = dbUtenti.update(credenzialiSql);
        if(!credenzialiBool)
            return "Errore nella creazione delle credenziali";

        System.out.println(utenteSql);
        boolean utenteBool = dbUtenti.update(utenteSql);
        if(!utenteBool)
            return "Errore nella creazione delle utente";

        return "Successo";
    }

    public boolean modificaDatiUtente(String username, Utente utente) {
        StringBuilder comandoSql = new StringBuilder("UPDATE Utente SET ");
        boolean primoCampo = true;

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
        comandoSql.append("WHERE username=\"" +username+ "\"");
        System.out.println(comandoSql);
        return dbUtenti.update(comandoSql.toString());
    }

    public boolean diventaPremium(String username) {
        String comandoSql = "UPDATE Utente SET tipo = 1 WHERE username=\"" +username+ "\"";
        System.out.println(comandoSql);
        return dbUtenti.update(comandoSql);
    }
}
