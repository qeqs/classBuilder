package tests.testClasses;

import main.java.annotations.TElement;
import main.java.annotations.TElements;

public class RootA {
    @TElement(name = "a")
    private int a;

    @TElement
    private String str;

    @TElement
    IncludedB bClass;
}
