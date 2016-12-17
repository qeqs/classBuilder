package main.java.db;

import main.java.Marshaller;
import main.java.annotations.TElement;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseMarshaller extends Marshaller {

    Connection connection;

    public void marshal(Object object, String name, Object proprety) throws Exception {
        connection = (Connection) proprety;
        Class<?> clazz = object.getClass();
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
            if (value.getClass().isPrimitive() || value.getClass().isAssignableFrom(String.class))
                preparedStatement.setObject(i, value);
            if (value.getClass().isArray() || value.getClass().isAssignableFrom(List.class)) {

            }
            if (value.getClass().isAssignableFrom(Map.class)) {

            }
        }

    }

    private List<String> getColoums(Class<?> clazz) {
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

    private String getColoumsString(List<String> coloums) {
        StringBuilder sb = new StringBuilder("(");
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
