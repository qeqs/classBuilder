package main.java;

import java.sql.Date;
import java.util.Collection;
import java.util.Map;

public abstract class Unmarshaller {

    protected ElementType getElementType(Class<?> cl) {
        if (cl.isPrimitive() || cl.isAssignableFrom(String.class) || cl.isAssignableFrom(Date.class) || cl.isAssignableFrom(Number.class) || cl.isAssignableFrom(Boolean.class))
            return ElementType.Primitive;
        else if (cl.isArray() || Collection.class.isAssignableFrom(cl)) {
            return ElementType.Collection;
        } else if (cl.isAssignableFrom(Map.class)) {
            return ElementType.Map;
        } else {
            return ElementType.Complex;
        }
    }
}
