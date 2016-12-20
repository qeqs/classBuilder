package xml;

public class Util {

    static boolean isNumberExtender(Class<?> objToCheck) {
        return Number.class.isAssignableFrom(objToCheck);
    }

    static boolean isCharacterExtender(Class<?> objToCheck) {
        return Character.class.isAssignableFrom(objToCheck) || objToCheck == char.class;
    }

    static boolean isMapEntry(Object objToCheck) {
        return (objToCheck instanceof MapEntry);
    }

    static boolean isStringExtender(Class<?> classToCheck) {
        return String.class.isAssignableFrom(classToCheck);
    }
}
