package tests.java.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class XMLUnmarshallerTest {

    @Test
    public void testParse() throws Exception {
        System.out.println("XMLUNMARSHALLER TEST �1");
        System.out.println("testing right unmarshalling to xml");
        TestSub testSub = new TestSub(78);
        TestSub testSub1 = new TestSub(14);
        TestSub testSub2 = new TestSub(2345);

        List<TestSub> testSubList = new ArrayList<>();
        testSubList.add(testSub1);
        testSubList.add(testSub2);

        Double[][] arr1 = new Double[2][2];
        char[][] arrc = new char[2][2];
        arrc[0][0] = 'e';
        arrc[0][1] = 'd';
        arrc[1][0] = 's';
        arrc[1][1] = 'q';
        arr1[0][0] = 1.5;
        arr1[0][1] = 2.3;
        arr1[1][0] = 1.29;
        arr1[1][1] = 4.4;

        Map<String, List<TestSub>> map = new HashMap<>();
        map.put("firstEntry", testSubList);
        map.put("secondEntry", testSubList);

        TestClass test = new TestClass(2.4, "checking it up", testSub, arrc, arr1, 's', testSubList, map);

        System.out.println("parse");

        TestClass parsedTest = (TestClass) new XMLUnmarshaller().parse("file.xml");
        assertEquals(test, parsedTest);
        System.out.println("parameters: XML with written Class Test that contains another class, arrays and maps with list field; Result: Ok");

    }

}
