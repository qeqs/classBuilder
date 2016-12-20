package xml;

import main.java.annotations.TElement;
import java.util.Objects;
import main.java.annotations.TRootElement;

@TRootElement(name = "TestClass")
public class TestClassWithEqualAnnoValues {

    @TElement(name = "equal")
    private double firstField;
    @TElement(name = "equal")
    TestSub classField;
    @TElement(name = "equal")
    String stringField;

    public TestClassWithEqualAnnoValues(double a, String str, TestSub testSub, char[][] arrc) {
        this.firstField = a;
        this.stringField = str;
        classField = testSub;

    }

    public TestClassWithEqualAnnoValues() {
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TestClassWithEqualAnnoValues)) {
            return false;
        }
        TestClassWithEqualAnnoValues that = (TestClassWithEqualAnnoValues) other;
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
        return hash;
    }
}
