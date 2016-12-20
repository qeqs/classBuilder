package main.java;

import java.util.*;

public abstract class Marshaller {


    public abstract void marshal(Object object, String name, Object proprety) throws Exception;

    protected ElementType getElementType(Class<?> cl) {
        if (cl.isPrimitive() || String.class.isAssignableFrom(cl) || Date.class.isAssignableFrom(cl) || Number.class.isAssignableFrom(cl) || Boolean.class.isAssignableFrom(cl))
            return ElementType.Primitive;
        else if (cl.isArray() || Collection.class.isAssignableFrom(cl)){//||cl.isAssignableFrom(List.class)||cl.isAssignableFrom(Set.class)) {
            return ElementType.Collection;
        } else if (Map.class.isAssignableFrom(cl)) {
            return ElementType.Map;
        } else {
            return ElementType.Complex;
        }
    }

}
