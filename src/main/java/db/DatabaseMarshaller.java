package main.java.db;

import main.java.Marshaller;
import main.java.annotations.TElement;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class DatabaseMarshaller extends Marshaller {

    Connection connection;

    public void marshal(Object object, String name, Object proprety) {
        connection = (Connection) proprety;
        Class<?> clazz = object.getClass();
        String tableName = clazz.getSimpleName();
        System.out.println(getColoums(clazz));
    }

    private List<String> getColoums(Class<?> clazz) {
        ArrayList<String> coloums = new ArrayList<String>();
        for (Field field : clazz.getDeclaredFields()
                ) {
            if (field.isAnnotationPresent(TElement.class)) {
                String name = field.getAnnotation(TElement.class).name();
                if (name.equals(""))
                    coloums.add(field.getName());
                else
                    coloums.add("");

            }
        }
        return coloums;
    }

}
