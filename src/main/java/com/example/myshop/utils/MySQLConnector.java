package com.example.myshop.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import static java.lang.Class.*;

public class MySQLConnector {
    String url = "jdbc:mysql://localhost:3306/mycafedb";
    Connection connection;
    public static MySQLConnector instance = null;
    public MySQLConnector(){
    }
    public static MySQLConnector getInstance(){
        if (instance ==null){
            instance = new MySQLConnector();
        }
        return instance;
    }
    public boolean Connect(String username, String password){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connect successful");
            return true;
        } catch (Exception  ex) {
            System.out.println(ex);
            System.out.println("Connect fail");
        }
        return false;
    }
    public boolean queryUpdate(String sql){
        if (connection != null){
            try{
                connection.createStatement().executeUpdate(sql);
                return true;
            }catch (Exception ex){
                System.out.println(ex);
                return false;
            }

        }
        return false;
    }
    public ResultSet queryResults(String sql){
        if (connection != null){
            try{
                return connection.createStatement().executeQuery(sql);
            }catch (Exception ex){
                System.out.println(ex);
            }

        }
        return null;
    }
}
