package main.java.db;

import main.java.Unmarshaller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUnmarshaller extends Unmarshaller{

    private static final String url = "jdbc:mysql://localhost:3306/RECIPES_DATABASE?useSSL=false&characterEncoding=UTF-8";
    private static final String user = "Annie";
    private static final String password = "Password1";

    public void unmarshall(Object clazz) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {




        } catch (SQLException ex) {
            System.out.println("Ошибка подключения к БД");
        }


    }
}
