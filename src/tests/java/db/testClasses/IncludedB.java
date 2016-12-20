package tests.java.db.testClasses;

import main.java.annotations.TElement;

import java.util.ArrayList;
import java.util.List;

public class IncludedB {
    @TElement
    private List<RootA> list = new ArrayList<>();
    @TElement
    public Integer c = 10;
}
