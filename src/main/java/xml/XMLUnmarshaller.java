package main.java.xml;

import main.java.annotations.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang3.ClassUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

interface Converter {

    Object method(String stringToConvert);
}

public class XMLUnmarshaller {

    Map<Class<?>, Converter> convertMap = new HashMap<>();

    public XMLUnmarshaller() {
        convertMap.put(Integer.class, (Converter) (String stringToConvert) -> Integer.parseInt(stringToConvert));
        convertMap.put(int.class, (Converter) (String stringToConvert) -> Integer.parseInt(stringToConvert));
        convertMap.put(Character.class, (Converter) (String stringToConvert) -> stringToConvert.charAt(0));
        convertMap.put(char.class, (Converter) (String stringToConvert) -> stringToConvert.charAt(0));
        convertMap.put(String.class, (Converter) (String stringToConvert) -> stringToConvert);
        convertMap.put(Byte.class, (Converter) (String stringToConvert) -> Byte.parseByte(stringToConvert));
        convertMap.put(byte.class, (Converter) (String stringToConvert) -> Byte.parseByte(stringToConvert));
        convertMap.put(Long.class, (Converter) (String stringToConvert) -> Long.getLong(stringToConvert));
        convertMap.put(Double.class, (Converter) (String stringToConvert) -> Double.parseDouble(stringToConvert));
        convertMap.put(double.class, (Converter) (String stringToConvert) -> Double.parseDouble(stringToConvert));
        //fill it 
    }

    public Stack<Object> stackBuilder = new Stack();

    public Object parse(String filePath) throws ParserConfigurationException, SAXException, IOException, VerifyError {

        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();

        DefaultHandler handler = new DefaultHandler() {
            String curField = null;
            int counterForPrimitiveArrays = 0;

            @Override
            public void startElement(String uri, String localName, String qName,
                    Attributes attributes) throws SAXException {
                String classType = attributes.getValue("type");
                if (classType != null) {
                    try {
                        Class<?> clazz = Class.forName(classType);
                        Object stringToConvertjectToCreate;
                        if (Util.isCharacterExtender(clazz)) {
                            stringToConvertjectToCreate = clazz.getDeclaredConstructor(char.class).newInstance('0');
                            curField = qName;
                        } else {
                            if (Util.isNumberExtender(clazz)) {
                                stringToConvertjectToCreate = clazz.getDeclaredConstructor(String.class).newInstance("0");
                                curField = qName;
                            } else {
                                if (clazz.isArray()) {
                                    Class<?> elemtype = ClassUtils.getClass(attributes.getValue("elemtype"));
                                    if (elemtype.isPrimitive()) {
                                        counterForPrimitiveArrays = 0;
                                    }
                                    stringToConvertjectToCreate = Array.newInstance(elemtype, Integer.parseInt(attributes.getValue("length")));
                                } else {
                                    if (Util.isStringExtender(clazz)) {
                                        stringToConvertjectToCreate = clazz.getDeclaredConstructor().newInstance();
                                        curField = qName;
                                    } else {
                                        if (Entry.class.isAssignableFrom(clazz)) {
                                            stringToConvertjectToCreate = new MapEntry();
                                        } else {
                                            Constructor<?> plainConstructor = clazz.getDeclaredConstructor();
                                            plainConstructor.setAccessible(true);
                                            stringToConvertjectToCreate = plainConstructor.newInstance();
                                            plainConstructor.setAccessible(false);
                                        }
                                    }
                                }
                            }
                        }
                        stackBuilder.push(stringToConvertjectToCreate);
                    } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NegativeArraySizeException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                        System.out.println("An error occured while parsing opening tag: " + qName + "; " + e.getMessage());
                    }
                } else {
                    curField = qName;
                }
            }

            @Override
            public void endElement(String uri, String localName,
                    String qName) throws SAXException {
                if (curField != null) {
                    curField = null;
                } else {
                    if (stackBuilder.size() > 1) {
                        Object lastElem = stackBuilder.pop();
                        Object preLastElem = stackBuilder.pop();
                        if (preLastElem instanceof Collection<?>) {
                            ((Collection<Object>) preLastElem).add(lastElem);
                        } else {
                            if (preLastElem instanceof Map<?, ?>) {
                                ((Map<Object, Object>) preLastElem).put(((MapEntry) lastElem).getKey(), ((MapEntry) lastElem).getValue());
                            } else {
                                if (Util.isMapEntry(preLastElem)) {
                                    if (((MapEntry) preLastElem).getKey() == null) {
                                        ((MapEntry) preLastElem).setKey(lastElem);
                                    } else {
                                        ((MapEntry) preLastElem).setValue(lastElem);
                                    }
                                } else {
                                    if (preLastElem.getClass().isArray()) {
                                        if (preLastElem.getClass().getComponentType().isPrimitive()) {
                                            Array.set(preLastElem, counterForPrimitiveArrays, lastElem);
                                            counterForPrimitiveArrays++;
                                        } else {
                                            int i = 0;
                                            while (i < Array.getLength(preLastElem)) {
                                                if (Array.get(preLastElem, i) == null) {
                                                    Array.set(preLastElem, i, lastElem);
                                                    break;
                                                }
                                                i++;
                                            }
                                        }
                                    } else {
                                        try {
                                            for (Field field : preLastElem.getClass().getDeclaredFields()) {
                                                if (field.isAnnotationPresent(TElement.class)) {
                                                    if (field.getAnnotation(TElement.class).name().equals(qName)) {
                                                        field.setAccessible(true);
                                                        field.set(preLastElem, lastElem);
                                                        field.setAccessible(false);
                                                        break;
                                                    }
                                                }
                                            }
                                        } catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
                                            System.out.println("An error occured while parsing closing tag: " + qName + "; " + e.getMessage());

                                        }
                                    }
                                }
                            }
                        }
                        stackBuilder.push(preLastElem);
                    }
                }
            }

            @Override
            public void characters(char ch[], int start, int length) throws SAXException {
                String valueFromXML = new String(ch, start, length);
                if (curField != null) {
                    try {
                        Object lastElem = stackBuilder.pop();
                        if (Util.isNumberExtender(lastElem.getClass()) || Util.isStringExtender(lastElem.getClass())) {
                            Constructor<?> plainConstructor = lastElem.getClass().getDeclaredConstructor(String.class);
                            lastElem = plainConstructor.newInstance(valueFromXML);
                            curField = null;
                        } else {
                            if (Util.isCharacterExtender(lastElem.getClass())) {
                                Constructor<?> plainConstructor = lastElem.getClass().getDeclaredConstructor(char.class);
                                lastElem = plainConstructor.newInstance(valueFromXML.charAt(0));
                                curField = null;
                            } else {
                                for (Field field : lastElem.getClass().getDeclaredFields()) {
                                    if (field.isAnnotationPresent(TElement.class)) {
                                        if (field.getAnnotation(TElement.class).name().equals(curField)) {
                                            field.setAccessible(true);
                                            field.set(lastElem, convertMap.get(field.getType()).method(valueFromXML));
                                            field.setAccessible(false);
                                        }
                                    }
                                }
                            }
                        }
                        stackBuilder.push(lastElem);
                    } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
                        System.out.println("An error occured while parsing value: " + valueFromXML + "; " + ex.getMessage());
                    }
                }
            }
        };

        saxParser.parse(new File(filePath), handler);
        return stackBuilder.pop();
    }

}
