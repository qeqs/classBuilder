package main.java;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

public abstract class Marshaller {


    public abstract void marshal(Object object, String name, Object proprety) throws Exception;

    protected ElementType getElementType(Class<?> cl) {
        if (cl.isPrimitive() || cl.isAssignableFrom(String.class) || cl.isAssignableFrom(Date.class) || cl.isAssignableFrom(Number.class) || cl.isAssignableFrom(Boolean.class))
            return ElementType.Primitive;
        else if (cl.isArray() || cl.isAssignableFrom(Collection.class)) {
            return ElementType.Collection;
        } else if (cl.isAssignableFrom(Map.class)) {
            return ElementType.Map;
        } else {
            return ElementType.Complex;
        }
    }

}
