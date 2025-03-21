package DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DbUtenti {
    public DbUtenti()
    {
    }

    public ArrayList<HashMap<String, Object>> query(String comandoSql)
    {
        Connection conn;
        Statement stmt;
        ResultSet rs;
        ArrayList<HashMap<String, Object>> list = null;
        int columns;
        HashMap<String, Object> row;
        ResultSetMetaData md;

        System.out.println("DbUtenti: " + comandoSql + "\n");

        try
        {
            //prova connessione da IDE, altrimenti prova connessione con progetto buildato
            try {
                conn = DriverManager.getConnection("jdbc:sqlite:dati/DbUtenti");
            } catch (SQLException e) {
                conn = DriverManager.getConnection("jdbc:sqlite:ParkCharge_Backend/dati/DbUtenti");
            }
            stmt = conn.createStatement();
            rs = stmt.executeQuery(comandoSql);
            md = rs.getMetaData();
            columns = md.getColumnCount();
            list = new ArrayList<HashMap<String, Object>>();
            while (rs.next())
            {
                row = new HashMap<String, Object>(columns);
                for(int i=1; i<=columns; ++i)
                    row.put(md.getColumnName(i), rs.getObject(i));
                list.add(row);
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return list;
    }

    public boolean update(String comandoSql)
    {
        Connection conn;
        Statement stmt;

        System.out.println("DbUtenti: " + comandoSql + "\n");

        try
        {
            try {
                conn = DriverManager.getConnection("jdbc:sqlite:dati/DbUtenti");
            } catch (SQLException e) {
                conn = DriverManager.getConnection("jdbc:sqlite:ParkCharge_Backend/dati/DbUtenti");
            }
            stmt = conn.createStatement();
            if(stmt.executeUpdate(comandoSql) == 0)
                return false;
            stmt.close();
            conn.close();
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

}
