import DataBase.DbStorico;

import java.util.HashMap;

public class GestorePagamenti {
    private DbStorico dbStorico;

    public GestorePagamenti(){
        this.dbStorico = new DbStorico();
    }

    public HashMap<String, Object> getCosti(){
        String comandoSql = "SELECT * FROM Costi";
        System.out.println(comandoSql);
        var rs = dbStorico.query(comandoSql);
        System.out.println(rs.get(0));
        return rs.get(0);
    }
}
