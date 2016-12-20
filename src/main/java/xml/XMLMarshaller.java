package xml;

import main.java.Marshaller;
import main.java.annotations.*;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class XMLMarshaller extends Marshaller {

    private static Element marshallList(Object listToMarshall, String name, Document doc) throws ParserConfigurationException, IllegalArgumentException, IllegalAccessException, Exception {
        Element listNode = doc.createElement(name);
        if (listToMarshall != null) {
            listNode.setAttribute("type", listToMarshall.getClass().getName());
            for (Object collectionElem : ((Collection<Object>) listToMarshall)) {
                listNode.appendChild(marshallObject(collectionElem, "element", doc));//possible to add annotated Name
            }
        }
        return listNode;
    }

    private static Element marshallArray(Object arrayToMarshall, String name, Document doc) throws ParserConfigurationException, IllegalArgumentException, IllegalAccessException, Exception {
        Element arrayNode = doc.createElement(name);
        int length = Array.getLength(arrayToMarshall);
        if (length > 0) {
            arrayNode.setAttribute("type", arrayToMarshall.getClass().getName());
            arrayNode.setAttribute("elemtype", arrayToMarshall.getClass().getComponentType().getName());
            arrayNode.setAttribute("length", String.valueOf(length));
            for (int i = 0; i < length; i++) {
                arrayNode.appendChild(marshallObject(Array.get(arrayToMarshall, i), "arrayElem", doc));
            }
        }
        return arrayNode;
    }

    private static Element marshallMap(Object mapToMarshall, String name, Document doc) throws ParserConfigurationException, IllegalArgumentException, IllegalAccessException, Exception {
        Element listNode = doc.createElement(name);
        if (mapToMarshall != null) {
            listNode.setAttribute("type", mapToMarshall.getClass().getName());
            for (Entry<Object, Object> collectionElem : ((Map<Object, Object>) mapToMarshall).entrySet()) {
                Element entry = doc.createElement("entry");
                entry.setAttribute("type", collectionElem.getClass().getName());
                entry.appendChild(marshallObject(collectionElem.getKey(), "key", doc));
                entry.appendChild(marshallObject(collectionElem.getValue(), "value", doc));
                listNode.appendChild(entry);
            }
        }
        return listNode;
    }

    @Override
    public void marshal(Object objectToMarshall, String name, Object filePath) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            doc.appendChild(marshallObject(objectToMarshall, name, doc));
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(new File((String) filePath)));
        } catch (Exception ex) {
            System.out.println("Problems with writing to XML: " + ex.toString());
        }
    }

    private static Element marshallObject(Object objectToMarshall, String name, Document doc) throws ParserConfigurationException, IllegalArgumentException, IllegalAccessException, Exception {
        if (objectToMarshall != null) {
            if (objectToMarshall instanceof Collection<?>) {
                return marshallList(objectToMarshall, name, doc);
            }
            if (objectToMarshall instanceof Map<?, ?>) {
                return marshallMap(objectToMarshall, name, doc);
            }

            if (objectToMarshall.getClass().isArray()) {
                return marshallArray(objectToMarshall, name, doc);
            }

            Element parent = doc.createElement(name);
            parent.setAttribute("type", objectToMarshall.getClass().getName());

            if (Util.isNumberExtender(objectToMarshall.getClass()) || Util.isCharacterExtender(objectToMarshall.getClass()) || Util.isStringExtender(objectToMarshall.getClass())) {
                parent.appendChild(doc.createTextNode(objectToMarshall != null ? ((objectToMarshall).toString()) : "null"));
                return parent;
            }
            if (PreProcessor.checkAnnotationsValues(objectToMarshall.getClass())) {
                for (Field field : objectToMarshall.getClass().getDeclaredFields()) {
                    if (field.getAnnotation(TElement.class) != null) {
                        field.setAccessible(true);
                        Element subElem = doc.createElement(field.getAnnotation(TElement.class).name().equals("") ? field.getName() : field.getAnnotation(TElement.class).name());
                        //if primitive - write, if complex- recursion. 
                        if (Util.isNumberExtender(field.get(objectToMarshall).getClass()) || Util.isCharacterExtender(field.get(objectToMarshall).getClass())) {
                            subElem.appendChild(doc.createTextNode(field.get(objectToMarshall).toString()));
                            parent.appendChild(subElem);
                        } else if (Util.isStringExtender(field.getType())) {
                            subElem.appendChild(doc.createTextNode(field.get(objectToMarshall) != null ? (field.get(objectToMarshall).toString()) : "null"));
                            parent.appendChild(subElem);
                        } else {
                            parent.appendChild(marshallObject(field.get(objectToMarshall), field.getAnnotation(TElement.class).name(), doc));
                        }
                        field.setAccessible(false);
                    }
                }
            } else {
                throw new Exception("Class " + objectToMarshall.getClass().getName() + " is bad-annotated!");
            }
            return parent;
        } else {
            throw new Exception("Null-object is present in input class!");
        }
    }
}
