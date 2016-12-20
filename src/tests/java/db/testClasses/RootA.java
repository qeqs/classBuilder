package tests.java.db.testClasses;

import main.java.annotations.TElement;

public class RootA {
    @TElement(name = "a")
    private int a;

    @TElement
    private String str;

    @TElement
    IncludedB bClass = new IncludedB();
}
