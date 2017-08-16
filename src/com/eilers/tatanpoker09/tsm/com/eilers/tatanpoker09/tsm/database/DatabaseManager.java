package com.eilers.tatanpoker09.tsm.com.eilers.tatanpoker09.tsm.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by tatanpoker09 on 29-07-2017.
 */
public class DatabaseManager {
    private Connection conn;
    public void DatabaseManager(){

    }


    public void startConnection(String host, String port, String database, String username, String password){
        MySQL mySQL = new MySQL(host, port, database, username, password);
        try {
            Connection c = mySQL.openConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void insert(String table, String[] cols, String[] values) throws SQLException {
        String sql = "INSERT INTO "+table+" () VALUES()";
        conn.prepareStatement(sql);
    }
}
