package com.eilers.tatanpoker09.tsm.database;

import com.eilers.tatanpoker09.tsm.Manager;
import com.eilers.tatanpoker09.tsm.server.Tree;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * Created by tatanpoker09 on 29-07-2017.
 */
public class DatabaseManager implements Manager, Callable {
    private static final String host = "localhost";
    private static final String port = "3306";
    private static final String database = "Tree";
    private static final String username = "root";
    private static final String password = "";


    private Connection conn;

    public void DatabaseManager() {

    }


    public boolean startConnection(String host, String port, String database, String username, String password) {
        MySQL mySQL = new MySQL(host, port, database, username, password);
        try {
            Connection c = mySQL.openConnection();
            this.conn = c;
            Logger log = Tree.getLog();
            log.info("Connected to MySQL succesfully!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setup() {
        return startConnection(host, port, database, username, password);
    }

    @Override
    public void postSetup() {

    }

    @Override
    public Object call() throws Exception {
        return null;
    }

    public void executeCommand(String sqlString) {
        try {
            if (conn.isClosed())
                startConnection(host, port, database, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            PreparedStatement s = conn.prepareStatement(sqlString);
            s.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PreparedStatement prepareStatement(String s) {
        try {
            return conn.prepareStatement(s);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet getFromDatabase(String s) {
        try {
            PreparedStatement ps = conn.prepareStatement(s);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
