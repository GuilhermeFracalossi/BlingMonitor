package org.database;

import org.CamerasConfig;

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

    public static void deleteCamera(Integer id) {
        ArrayList<String> params = new ArrayList<>();

        params.add(String.valueOf(id));
        execute("DELETE FROM cameras WHERE id=?",params,false);
    }

    public void prepareDatabase() {
        super.createDefaultTables();

        if (super.isDefaultTablesEmpty()) {
            super.insertDefaultUser();
        }
        closeConn();
    }

    public static ResultSet login(String user, String password) {

        ArrayList<String> params = new ArrayList<>();

        params.add(user);
        params.add(password);

        return execute("SELECT * FROM usuario WHERE login=? AND senha=?", params,true);
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

            md = MessageDigest.getInstance("SHA-256");
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
        return execute("SELECT * FROM usuario",true);
    }
    public static Long insertCamera(CamerasConfig cameraObj){
        try {
            ArrayList params = new ArrayList();

            params.add(cameraObj.getName().trim());
            params.add(cameraObj.getAddress().trim());
            params.add(String.valueOf(cameraObj.getPort()));
            params.add(String.valueOf(cameraObj.getBrightness()));
            params.add(String.valueOf(cameraObj.getGamma()));
            params.add(String.valueOf(cameraObj.getSaturation()));
            params.add(String.valueOf(cameraObj.getContrast()));

            execute("INSERT INTO cameras(name,address,port,brightness,gamma,saturation,contrast) values(?,?,?,?,?,?,?)", params, false);
            return lastInsertedId();

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public static void updateCamera(CamerasConfig cameraObj){
        ArrayList params = new ArrayList();
        params.add(cameraObj.getName().trim());
        params.add(cameraObj.getAddress().trim());
        params.add(cameraObj.getPort());
        params.add(cameraObj.getBrightness());
        params.add(cameraObj.getGamma());
        params.add(cameraObj.getSaturation());
        params.add(cameraObj.getContrast());

        params.add(cameraObj.getId());

        execute("UPDATE cameras SET name=?,address=?,port=?,brightness=?,gamma=?,saturation=?,contrast=? WHERE id=?",params, false);
    }
}