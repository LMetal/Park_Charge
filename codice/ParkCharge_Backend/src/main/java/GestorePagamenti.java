import DataBase.DbStorico;
import DataBase.DbUtenti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorePagamenti {
    private DbStorico dbStorico;

    // Costruttore
    public GestorePagamenti(){
        this.dbStorico = new DbStorico();
    }

    // Metodo per ottenere tutti i costi dal database
    public ArrayList<HashMap<String, Object>> getCosti(){
        String comandoSql = "SELECT * FROM Costi";
        System.out.println(comandoSql);

        // Esegue la query per ottenere i costi dal database storico
        var rs = dbStorico.query(comandoSql);
        return rs;
    }

    // Metodo per ottenere il costo del servizio premium dal database
    public int getCostoPremium() {
        String comandoSql = "SELECT costo_premium FROM Costi";
        System.out.println(comandoSql);

        // Esegue la query per ottenere il costo del servizio premium dal database storico
        ArrayList<HashMap<String, Object>> rs = dbStorico.query(comandoSql);

        // Restituisce il costo premium come intero
        return (Integer) rs.get(0).get("costo_premium");
    }

    // Metodo per aggiornare i prezzi nel database
    public boolean aggiornaPrezzi(Costi costo) {
        // Costruisce la query di aggiornamento dei prezzi con i dati forniti dall'oggetto Costi
        String comandoSql = "UPDATE Costi SET costo_posteggio = \"" + costo.getCosto_posteggio() + "\", costo_ricarica = \"" + costo.getCosto_ricarica() + "\", penale = \"" + costo.getPenale() + "\", costo_premium = \"" + costo.getCosto_premium() + "\" WHERE id = \"" + 1 + "\";";
        System.out.println(comandoSql);

        // Esegue la query di aggiornamento nel database storico e restituisce il risultato
        return dbStorico.update(comandoSql);
    }

    public ArrayList<HashMap<String, Object>> getStorico() {
        String comandoSql = "SELECT * FROM Pagamenti\n" +
                "JOIN PrezzoPosteggio \n" +
                "WHERE Pagamenti.costo = PrezzoPosteggio.id";

        return dbStorico.query(comandoSql);
    }
}
