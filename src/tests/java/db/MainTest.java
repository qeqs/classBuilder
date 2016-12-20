package tests.java.db;

import main.java.db.DatabaseMarshaller;
import org.junit.Test;
import tests.java.db.testClasses.RootA;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;


public class MainTest {

    @Test
    public void test() {
        DatabaseMarshaller marshaller = new DatabaseMarshaller();
        List<RootA> list = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            list.add(new RootA());
        Connection connection = null;

            try {
                Class.forName("org.postgresql.Driver");

                connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/classBuilder", "postgres", "110371");

                marshaller.marshal(list, "RootAList", connection);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null)
                        connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }


        }
    }

