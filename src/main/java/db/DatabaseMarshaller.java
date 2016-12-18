package main.java.db;

import main.java.ElementType;
import main.java.Marshaller;
import main.java.annotations.TElement;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DatabaseMarshaller extends Marshaller {

    Connection connection;

    public void marshal(Object object, String name, Object proprety) throws Exception {
        connection = (Connection) proprety;
        marshalObject(object, name);
    }

    public int marshalObject(Object object, String name) throws Exception {

        Class<?> clazz = object.getClass();
        if (getElementType(clazz).equals(ElementType.Collection)) {
            return marshalCollection(object, name);
        }
        if (getElementType(clazz).equals(ElementType.Map)) {
            return marshalMap(object, name);
        }
        String tableName = clazz.getSimpleName();
        List<Object> values = getValues(object);
        List<String> coloums = getColoums(clazz);
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("INSERT INTO ");
        sqlQuery.append(tableName);
        sqlQuery.append(getColoumsString(coloums));
        sqlQuery.append("VALUES ");
        sqlQuery.append("(");
        for (String str : coloums
                ) {
            sqlQuery.append("?,");
        }
        sqlQuery.deleteCharAt(sqlQuery.length() - 1);
        sqlQuery.append(")");
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery.toString());
        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            switch (getElementType(value.getClass())) {
                case Primitive:
                    preparedStatement.setObject(i, value);
                    break;
                case Collection:
                    preparedStatement.setInt(i, marshalCollection(value, name));
                    break;
                case Map:
                    preparedStatement.setInt(i, marshalMap(value, name));
                    break;
                case Complex:
                    preparedStatement.setInt(i, marshalObject(value, name));
                    break;
                default:
                    preparedStatement.setObject(i, null);
                    break;
            }
        }
        if (preparedStatement != null)
            preparedStatement.execute();

        preparedStatement = connection.prepareStatement("SELECT max(id) FROM " + tableName + ";");
        ResultSet rs = preparedStatement.executeQuery();
        rs.next();
        return rs.getInt(1);

    }

    private int marshalCollection(Object object, String name) throws Exception {

        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Collection(name,type,value) VALUES(?,?,?);");
        preparedStatement.setString(1, name);
        if (object.getClass().isArray()) {
            preparedStatement.setString(2, Array.class.getName());
            preparedStatement.setString(3, object.getClass().getComponentType().getName());
            for (Object obj : (Object[]) object
                    ) {
                marshalObject(obj, null);
            }
        } else {
            preparedStatement.setString(2, object.getClass().getName());
            Class<?> cl = null;
            for (Object obj : (Collection<?>) object
                    ) {

                marshalObject(obj, null);
                cl = obj.getClass();
            }
            preparedStatement.setString(3, cl == null ? null : cl.getName());
        }
        preparedStatement.execute();


        preparedStatement = connection.prepareStatement("SELECT max(id) FROM Collections;");
        ResultSet rs = preparedStatement.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    private int marshalMap(Object object, String name) throws Exception {

        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Collection(name,type,key,value) VALUES(?,?,?,?);");
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, object.getClass().getName());
        Class<?> key = null;
        Class<?> value = null;
        for (Map.Entry<?, ?> obj : ((Map<?, ?>) object).entrySet()
                ) {
            marshalObject(obj.getKey(), null);
            key = obj.getKey().getClass();

            marshalObject(obj.getValue(), null);
            value = obj.getValue().getClass();
        }
        preparedStatement.setString(3, key == null ? null : key.getName());
        preparedStatement.setString(4, value == null ? null : value.getName());

        preparedStatement = connection.prepareStatement("SELECT max(id) FROM Map;");
        ResultSet rs = preparedStatement.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public List<String> getColoums(Class<?> clazz) {
        ArrayList<String> coloums = new ArrayList<String>();


        for (Field field : clazz.getDeclaredFields()
                ) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(TElement.class)) {
                String name = field.getAnnotation(TElement.class).name();
                if (name.equals(""))
                    coloums.add(field.getName());
                else
                    coloums.add(name);
            }
            field.setAccessible(false);
        }
        return coloums;
    }

    public String getColoumsString(List<String> coloums) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (String coloum : coloums) {
            sb.append(coloum);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

    private List<Object> getValues(Object object) throws IllegalAccessException {
        ArrayList<Object> values = new ArrayList<Object>();
        for (Field field : object.getClass().getDeclaredFields()
                ) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(TElement.class)) {
                values.add(field.get(object));
            }
            field.setAccessible(false);
        }
        return values;
    }

}
