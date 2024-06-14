import DataBase.DbStorico;
import DataBase.DbUtenti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorePagamenti {
    private DbStorico dbStorico;

    public GestorePagamenti(){
        this.dbStorico = new DbStorico();
    }

    public int getCostoPremium() {
        String comandoSql = "SELECT costo_premium FROM Costi";
        System.out.println(comandoSql);
        ArrayList<HashMap<String, Object>> rs = dbStorico.query(comandoSql);

        return (Integer) rs.get(0).get("costo_premium");
    }
}
