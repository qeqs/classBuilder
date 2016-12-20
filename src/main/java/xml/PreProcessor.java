package main.java.xml;

import main.java.annotations.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class PreProcessor {
//Check whether class is Troot... annotated and check that all Telement names are not equal
    static public boolean checkAnnotationsValues(Class<?> clazz) {
        List<String> collection = new ArrayList<>();
        if (clazz.getAnnotation(TElement.class) != null || clazz.getAnnotation(TRootElement.class) != null) {
            collection.add(clazz.getAnnotation(TElement.class) == null ? clazz.getAnnotation(TRootElement.class).name() : clazz.getAnnotation(TElement.class).name());
            for (Field field : clazz.getDeclaredFields()) {
                TElement a = field.getAnnotation(TElement.class);
                if (a != null) {
                    if (collection.contains(a.name())) {
                        return false;
                    }
                    collection.add(a.name());
                }
            }
            return true;
        }
        return false;
    }
}
