package org.database;

import javax.xml.transform.Result;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

public class Database extends MySQL {

    public Database() {
//        calls the parent class constructor to connect to the database
        super();
    }

    public void prepareDatabase() {
        super.createDefaultTables();

        if (super.isDefaultTablesEmpty()) {
            super.insertDefaultUser();
        }
        try {
            ResultSet a = debugTable("usuario");
        }catch (SQLException e){
            e.printStackTrace();
        }
        super.closeConn();
    }

    public static ResultSet login(String user, String password) {

        ArrayList<String> params = new ArrayList<String>();

        params.add(user);
        params.add(password);

        return execute("SELECT * FROM usuario WHERE login=? AND senha=?", params,true);
    }
    public static ResultSet debugTable(String table) throws SQLException {
        ResultSet resultSet = execute("SELECT * FROM usuario", true);
        System.out.println(resultSet.getObject(1));
        return resultSet;
    }
    public static void updateUserInfo(int id, String nome, String login, String senha) {
        ArrayList params = new ArrayList();

        params.add(nome);
        params.add(login);
        params.add(senha);
        params.add(String.valueOf(id));

        execute("UPDATE usuario SET nome=?, login=?, senha=? WHERE id=?", params, false);
    }


    public static String encrypt(String password) {
        String result = password;
        MessageDigest md;

        try {

            md = MessageDigest.getInstance("MD5");
            BigInteger hash = new BigInteger(1, md.digest(password.getBytes()));
            result = hash.toString(16);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static boolean isClosed() throws SQLException {
        return isConnClosed();
    }

    public static void closeConnection() {
        closeConn();
    }

    public static ResultSet getUsers() {
        ResultSet data = null;

        ArrayList params = new ArrayList();

        data = execute("SELECT * FROM usuario", params,true);
        return data;
    }
}