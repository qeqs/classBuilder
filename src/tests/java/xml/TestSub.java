package tests.java.xml;

import main.java.annotations.TElement;
import main.java.annotations.TRootElement;

@TRootElement(name = "hellaSub")
public class TestSub {

    @TElement(name = "Int")
    int innerInt;

    public TestSub(int i) {
        this.innerInt = i;
    }

    TestSub() {
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TestSub)) {
            return false;
        }
        TestSub that = (TestSub) other;
        return this.innerInt == that.innerInt;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + this.innerInt;
        return hash;
    }
}
