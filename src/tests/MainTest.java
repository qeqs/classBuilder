package tests;

import main.java.db.DatabaseMarshaller;
import org.junit.Test;
import tests.testClasses.RootA;


public class MainTest {

    @Test
    public void test() {
        DatabaseMarshaller marshaller = new DatabaseMarshaller();
        RootA[] test = new RootA[10];
        System.out.println(test.getClass().getName());
        try {
            //  marshaller.marshal(new RootA(), "name", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

