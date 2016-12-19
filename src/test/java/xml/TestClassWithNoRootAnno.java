package xml;

import main.java.annotations.TElement;
import java.util.Arrays;
import java.util.Objects;

public class TestClassWithNoRootAnno {

    @TElement(name = "doubleFieldName")
    private double firstField;
    @TElement(name = "subClassInTest")
    TestSub classField;
    @TElement(name = "fieldWithString")
    String stringField;
    @TElement(name = "ArrayWithDoubles")
    Double[][] arrd;

    public TestClassWithNoRootAnno(double a, String str, TestSub testSub, char[][] arrc, Double[][] arrd) {
        this.firstField = a;
        this.stringField = str;
        classField = testSub;
        this.arrd = arrd;
    }

    public TestClassWithNoRootAnno() {
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TestClassWithNoRootAnno)) {
            return false;
        }
        TestClassWithNoRootAnno that = (TestClassWithNoRootAnno) other;
        return this.firstField == (that.firstField)
                && this.classField.equals(that.classField)
                && this.stringField.equals(that.stringField);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.firstField) ^ (Double.doubleToLongBits(this.firstField) >>> 32));
        hash = 83 * hash + Objects.hashCode(this.classField);
        hash = 83 * hash + Objects.hashCode(this.stringField);
        hash = 83 * hash + Arrays.deepHashCode(this.arrd);
        return hash;
    }
}
