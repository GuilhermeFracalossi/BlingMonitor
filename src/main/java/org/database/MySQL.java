package org.database;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class MySQL{
    private static Connection conn;
    private static Statement st;
    private static ResultSet rs = null;

    private static final String servidor = "jdbc:sqlite:monitor.db"; //database monitor
    private static final String driver = "org.sqlite.JDBC";

    public MySQL(){
        connect();
    }

    public static ResultSet execute(String query, ArrayList params, boolean expectResults){
//       Executes a query
        rs = null;
        try {
            PreparedStatement ps = conn.prepareStatement(query);

            //Adds all the parameters
            for (int i = 1; i <= params.size(); i++){
                String param = String.valueOf(params.get(i - 1));
                ps.setString(i, (String) params.get(i - 1));
            }

            if (expectResults) {
                rs = ps.executeQuery();
            }else{
                ps.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    public static ResultSet execute(String query, boolean expectResults){
        rs = null;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    private static void connect(){
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(servidor);
            st = conn.createStatement();

        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }

    protected void createDefaultTables() {
        try{
            String sql = "CREATE TABLE IF NOT EXISTS usuario (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nome VARCHAR(20)," +
                    "login VARCHAR(20)," +
                    "senha VARCHAR(32)" +
                    ")";
            st.execute(sql);

            sql =  "CREATE TABLE IF NOT EXISTS cameras("+
                    "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "cameraName VARCHAR(255),"+
                    "port INTEGER,"+
                    "address VARCHAR(15),"+
                    "gamma INTEGER,"+
                    "contrast INTEGER,"+
                    "brightness INTEGER"+
                    ")";
            st.execute(sql);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected boolean isDefaultTablesEmpty() {
        boolean empty = true;
        try {
            st = conn.createStatement();
            rs = st.executeQuery("SELECT COUNT(*) FROM usuario");

            while (rs.next()) {
                if(rs.getInt(1) > 0){
                    empty = false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empty;
    }

    protected void insertDefaultUser() {
        ArrayList<String> params = new ArrayList<String>();

        params.add("Nome Usuario");
        params.add("admin");
        params.add("21232f297a57a5a743894a0e4a801fc3");

        execute("INSERT INTO usuario(nome,login,senha) VALUES (?, ?, ?)",params,false);
    }
    protected static boolean isConnClosed() throws SQLException {
        return conn.isClosed();
    }
    protected static void closeConn() {
        try{
            if(st!=null)
                st.close();
        }catch(SQLException se){
            se.printStackTrace();
        }

        try{
            if(conn!=null)
                conn.close();
        }catch(SQLException se2){
            se2.printStackTrace();
        }
    }
}