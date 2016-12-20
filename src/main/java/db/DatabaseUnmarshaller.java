package main.java.db;

import main.java.annotations.TElement;
import main.java.annotations.TRootElement;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

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

                    if (Collection.class.isAssignableFrom(fieldClass)) {
                        Collection collection = unmarshallCollection(rs.getInt(fieldNameInDB), field.get(obj), connection);
                        field.set(obj, collection);
                    } else if (fieldClass.isAnnotationPresent(TRootElement.class)) {
                        Object fieldInstance = field.get(obj);
                        unmarshall(fieldInstance, rs.getInt(fieldNameInDB), connection);
                        field.set(obj, fieldInstance);
                    } else if (fieldClass.isArray()) {

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

    private void unmarshall(Object obj, int id, Connection connection) throws SQLException, IllegalAccessException {
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

    private Collection unmarshallCollection(int id, Object collectionObject, Connection connection) throws SQLException, IllegalAccessException {

        //получаем сведения о коллекции
        PreparedStatement statement = connection.prepareStatement("select * from  Collection where id=? ");
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();
        rs.next();
        String tableName = rs.getString("value");

        //получаем содержимое коллекции
        statement = connection.prepareStatement("select * from " + tableName + " where collection_id=? ");
        statement.setInt(1, id);
        ResultSet rs1 = statement.executeQuery();

        Collection collection = (Collection) collectionObject;
        Iterator<Connection> iterator = collection.iterator();

        while (rs1.next()) {
            Object current = iterator.next();
            int currentId = rs1.getInt("id");
            unmarshall(current, currentId, connection);

        }
        return collection;
    }

}
