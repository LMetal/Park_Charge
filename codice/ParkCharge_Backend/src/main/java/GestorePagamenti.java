import DataBase.DbStorico;

import java.util.ArrayList;
import java.util.HashMap;

public class GestorePagamenti {
    private DbStorico dbStorico;

    public GestorePagamenti(){
        this.dbStorico = new DbStorico();
    }

    public ArrayList<HashMap<String, Object>> getCosti(){
        String comandoSql = "SELECT * FROM Costi";
        System.out.println(comandoSql);
        var rs = dbStorico.query(comandoSql);
        return rs;
    }
}
