package main.java.db;

import main.java.Unmarshaller;
import main.java.annotations.TElement;
import main.java.annotations.TRootElement;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class DatabaseUnmarshaller extends Unmarshaller {

    Connection connection;

    public void marshal(Object object, String name, Object proprety) throws SQLException, IllegalAccessException {
        connection = (Connection) proprety;
        unmarshall(object, name);
    }

    public void unmarshall(Object obj, String name) throws SQLException, IllegalAccessException {
        if (obj.getClass().isAnnotationPresent(TRootElement.class)) {
            String tableName = obj.getClass().getAnnotation(TRootElement.class).name();

            //получаем информацию об объекте из бд по name
            PreparedStatement statement = connection.prepareStatement("select * from " + tableName + " where name=? ");
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            rs.next();

            //анмаршал объекта
            baseUnmarshall(obj, rs);

            statement.close();
            rs.close();

        }
    }

    private void unmarshall(Object obj, int id) throws SQLException, IllegalAccessException {
        if (obj.getClass().isAnnotationPresent(TRootElement.class)) {
            String tableName = obj.getClass().getAnnotation(TRootElement.class).name();

            //получаем информацию об объекте из бд по id
            PreparedStatement statement = connection.prepareStatement("select * from " + tableName + " where id=? ");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            rs.next();

            //анмаршал объекта
            baseUnmarshall(obj, rs);

            statement.close();
            rs.close();

        }
    }

    private void baseUnmarshall(Object obj, ResultSet rs) throws SQLException, IllegalAccessException {
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(TElement.class)) {
                field.setAccessible(true);

                Class fieldClass = field.getType();
                String fieldNameInDB = field.getAnnotation(TElement.class).name();

                switch (getElementType(fieldClass)) {
                    case Primitive:
                        Object result = rs.getObject(fieldNameInDB);
                        field.set(obj, result);
                        break;
                    case Collection:
                        if (fieldClass.isArray()) {
                            Object[] array = unmarshalArray(rs.getInt(fieldNameInDB), field.get(obj));
                            field.set(obj, array);
                        } else {
                            Collection collection = unmarshallCollection(rs.getInt(fieldNameInDB), field.get(obj));
                            field.set(obj, collection);
                        }
                        break;
                    case Complex:
                        if (fieldClass.isAnnotationPresent(TRootElement.class)) {
                            Object fieldInstance = field.get(obj);
                            unmarshall(fieldInstance, rs.getInt(fieldNameInDB));
                            field.set(obj, fieldInstance);
                        }
                        break;
                    case Map:
                        Map<?, ?> map = unmarshallMap(rs.getInt(fieldNameInDB), field.get(obj));
                        field.set(obj, map);
                        break;
                }

            }
        }

    }

    private Collection unmarshallCollection(int id, Object collectionObject) throws SQLException, IllegalAccessException {

        ResultSet rs = baseCollectionAndArrayUnmarhall(id);
        Collection collection = (Collection) collectionObject;
        Iterator<Collection> iterator = collection.iterator();

        while (rs.next()) {
            Object current = iterator.next();
            int currentId = rs.getInt("id");
            unmarshall(current, currentId);
        }
        rs.close();
        return collection;
    }


    private Object[] unmarshalArray(int id, Object collectionObject) throws SQLException, IllegalAccessException {

        ResultSet rs = baseCollectionAndArrayUnmarhall(id);

        Object[] array = (Object[]) collectionObject;
        int i = 0;
        while (rs.next()) {
            Object current = array[i];
            int currentId = rs.getInt("id");
            unmarshall(current, currentId);
            i++;
        }
        rs.close();
        return array;

    }

    private ResultSet baseCollectionAndArrayUnmarhall(int id) throws SQLException {
        //получаем сведения о коллекции
        PreparedStatement statement = connection.prepareStatement("select * from  Collection where id=? ");
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();
        rs.next();
        String tableName = rs.getString("value");
        rs.close();

        //получаем содержимое коллекции
        statement = connection.prepareStatement("select * from " + tableName + " where collection_id=? ");
        statement.setInt(1, id);
        ResultSet rs1 = statement.executeQuery();
        return rs1;

    }

    private Map<?, ?> unmarshallMap(int id, Object objectMap) throws SQLException, IllegalAccessException {
        PreparedStatement statement = connection.prepareStatement("select * from  Map where id=? ");
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();
        rs.next();
        String tableKeyName = rs.getString("key");
        String tableValueName = rs.getString("value");
        rs.close();

        statement = connection.prepareStatement("select * from " + tableKeyName + " where map_id=? ");
        statement.setInt(1, id);
        ResultSet rs1 = statement.executeQuery();

        statement = connection.prepareStatement("select * from " + tableValueName + " where map_id=? ");
        statement.setInt(1, id);
        ResultSet rs2 = statement.executeQuery();

        Map<?, ?> map = (Map<?, ?>) objectMap;

        for (Map.Entry<?, ?> obj : map.entrySet()) {
            rs1.next();
            rs2.next();

            int currentKeyId = rs1.getInt("id");
            int currentValue = rs2.getInt("id");
            unmarshall(obj.getKey(), currentKeyId);
            unmarshall(obj.getValue(), currentValue);
        }

        rs1.close();
        rs2.close();
        return map;
    }
}
