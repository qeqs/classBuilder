package tests;

import main.java.db.DatabaseMarshaller;
import org.junit.Test;
import tests.testClasses.RootA;


public class MainTest {

    @Test
    public void test() {
        DatabaseMarshaller marshaller = new DatabaseMarshaller();
        marshaller.marshal(new RootA(), "name", null);
    }
}

