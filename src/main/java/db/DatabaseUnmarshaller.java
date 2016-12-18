package db;

import annotations.TElement;
import annotations.TRootElement;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUnmarshaller {


    public void unmarshall(Object obj, String name, Connection connection) throws SQLException, IllegalAccessException, InstantiationException {
        if (obj.getClass().isAnnotationPresent(TRootElement.class)) {
            String tableName = obj.getClass().getAnnotation(TRootElement.class).name();
            PreparedStatement statement = connection.prepareStatement("select * from " + tableName + " where name=? ");
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            rs.next();

            for (Field field : obj.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(TElement.class)) {
                    field.setAccessible(true);

                    Class fieldClass = field.getType();
                    String fieldNameInDB = field.getAnnotation(TElement.class).name();

                    if (fieldClass.isAnnotationPresent(TRootElement.class)) {
                        //  Object fieldInstance = fieldClass.newInstance();
                        Object fieldInstance = field.get(obj);
                        unmarshall(fieldInstance, rs.getInt(fieldNameInDB), connection);
                        field.set(obj, fieldInstance);
                    } else {
                        Object result = rs.getObject(fieldNameInDB);
                        field.set(obj, result);
                    }
                }
            }
            statement.close();
            rs.close();

        }
    }

    private void unmarshall(Object obj, int id, Connection connection) throws SQLException, IllegalAccessException, InstantiationException {
        if (obj.getClass().isAnnotationPresent(TRootElement.class)) {
            String tableName = obj.getClass().getAnnotation(TRootElement.class).name();
            PreparedStatement statement = connection.prepareStatement("select * from " + tableName + " where id=? ");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            rs.next();

            for (Field field : obj.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(TElement.class)) {
                    field.setAccessible(true);

                    Class fieldClass = field.getType();
                    String fieldNameInDB = field.getAnnotation(TElement.class).name();

                    if (fieldClass.isAnnotationPresent(TRootElement.class)) {
                        Object fieldInstance = field.get(obj);
                        unmarshall(fieldInstance, rs.getInt(fieldNameInDB), connection);
                        field.set(obj, fieldInstance);
                    } else {
                        Object result = rs.getObject(fieldNameInDB);
                        field.set(obj, result);
                    }
                }
            }
            statement.close();
            rs.close();

        }
    }

}
