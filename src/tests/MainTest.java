package tests;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.org.apache.xpath.internal.operations.Number;
import main.java.db.DatabaseMarshaller;
import org.junit.Test;
import tests.testClasses.RootA;

import java.util.*;


public class MainTest {

    @Test
    public void test() {
        DatabaseMarshaller marshaller = new DatabaseMarshaller();
        RootA[] test = new RootA[10];
        List<RootA> list = new ArrayList<>();
        Set<RootA> set = new HashSet<>();
        list.add(new RootA());
        set.add(new RootA());
        for (Object obj : set
                ) {

            System.out.println(obj.getClass().getName());
        }
        try {
            //  marshaller.marshal(new RootA(), "name", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

