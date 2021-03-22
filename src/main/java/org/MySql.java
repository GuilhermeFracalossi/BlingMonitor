package org;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public  class MySql {

    public static Connection conn;
    public static Statement st;
    public static ResultSet rs = null;

    public static final String servidor = "jdbc:sqlite:src/main/resources/monitor.db"; //database monitor

    public static final String driver = "org.sqlite.JDBC";

    public void prepareAll(){

        autocrateTable();

        if(emptyTable()){
            insert();
        }
        FecharConexao();
    }


    public void autocrateTable(){
        try{
            String sql = "CREATE TABLE IF NOT EXISTS usuario (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nome VARCHAR(20)," +
                    "login VARCHAR(20)," +
                    "senha VARCHAR(32)" +
                    ")";
            st.execute(sql);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean emptyTable(){
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

    public void insert(){
        try{

            PreparedStatement ps;
            String sql = "INSERT INTO usuario(nome,login,senha) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sql);

            ps.setString(1, "Nome Usuario");
            ps.setString(2, "admin");
            ps.setString(3, "21232f297a57a5a743894a0e4a801fc3");

            ps.execute();

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public static ResultSet login(String usuario, String senha){
        rs = null;
        try {

            String query = "SELECT * FROM usuario WHERE login=? AND senha=?";


            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, usuario);
            ps.setString(2, senha);

            rs = ps.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }


    public static void update(String nome, String login, String senha, int id){
        try {

            PreparedStatement query = conn.prepareStatement("UPDATE usuario SET nome=?, login=?, senha=? WHERE id=?");

            query.setString(1,nome);
            query.setString(2,login);
            query.setString(3,senha);
            query.setInt(4, id);

            query.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
//        finally{
//            FecharConexao();
//        }
    }


    public static ResultSet getData(){
        ResultSet data = null;
        try {

            String query = "SELECT * FROM usuario";


            PreparedStatement ps = conn.prepareStatement(query);

            data = ps.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return data;
    }

    public static String encrypt(String password){
        String result = password;
        MessageDigest md;

        try{

            md = MessageDigest.getInstance("MD5");
            BigInteger hash = new BigInteger(1,md.digest(password.getBytes()));
            result = hash.toString(16);

        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        return result;
    }

    public MySql(){
        try {

            Class.forName(driver);
            conn = DriverManager.getConnection(servidor);

            st = conn.createStatement();

        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }

    public static void FecharConexao() {
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