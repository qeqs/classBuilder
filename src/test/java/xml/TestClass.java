package xml;

import main.java.annotations.TElement;
import main.java.annotations.TRootElement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@TRootElement(name = "hellaTest")
public class TestClass {

    @TElement(name = "doubleFieldName")
    private double firstField;
    @TElement(name = "subClassInTest")
    TestSub classField;
    @TElement(name = "fieldWithString")
    String stringField;
    @TElement(name = "ArrayWithDoubles")
    Double[][] arrd;
    @TElement(name = "ArrrayWithChars")
    char[][] arrc;
    @TElement(name = "fieldWithChar")
    char charField;
    @TElement(name = "ListOfTestSub")
    List<TestSub> testSubList;
    @TElement(name = "MapWithStringMappedOnList")
    Map<String, List<TestSub>> testSubMap;

    public TestClass(double a, String str, TestSub testSub, char[][] arrc, Double[][] arrd, char c, List<TestSub> list, Map<String, List<TestSub>> map) {
        this.firstField = a;
        this.stringField = str;
        classField = testSub;
        this.arrd = arrd;
        this.arrc = arrc;
        charField = c;
        testSubList = list;
        testSubMap = map;
    }

    public TestClass() {
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TestClass)) {
            return false;
        }
        TestClass that = (TestClass) other;
        return this.firstField==(that.firstField)&& 
                this.classField.equals(that.classField) &&
                this.charField==that.charField &&
                this.stringField.equals(that.stringField);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.firstField) ^ (Double.doubleToLongBits(this.firstField) >>> 32));
        hash = 83 * hash + Objects.hashCode(this.classField);
        hash = 83 * hash + Objects.hashCode(this.stringField);
        hash = 83 * hash + Arrays.deepHashCode(this.arrd);
        hash = 83 * hash + Arrays.deepHashCode(this.arrc);
        hash = 83 * hash + this.charField;
        hash = 83 * hash + Objects.hashCode(this.testSubList);
        hash = 83 * hash + Objects.hashCode(this.testSubMap);
        return hash;
    }
}
