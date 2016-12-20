package tests.java.xml;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class XMLMarshallerTest {

    public XMLMarshallerTest() {
    }

    @Test
    public void testMarshall() throws Exception {
        System.out.println("XMLMARSHALLER TEST �1");
        System.out.println("testing right marshalling to xml");

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
        String name = "Test";
        Object filePath = "file.xml";

        XMLMarshaller instance = new XMLMarshaller();

        instance.marshal(test, name, filePath);

        FileInputStream inFile = new FileInputStream((String) filePath);
        byte[] str = new byte[inFile.available()];
        inFile.read(str);
        String fileContent = new String(str);
        String rightResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><Test type=\"TestClass\"><doubleFieldName>2.4</doubleFieldName><subClassInTest type=\"TestSub\"><Int>78</Int></subClassInTest><fieldWithString>checking it up</fieldWithString><ArrayWithDoubles elemtype=\"[Ljava.lang.Double;\" length=\"2\" type=\"[[Ljava.lang.Double;\"><arrayElem elemtype=\"java.lang.Double\" length=\"2\" type=\"[Ljava.lang.Double;\"><arrayElem type=\"java.lang.Double\">1.5</arrayElem><arrayElem type=\"java.lang.Double\">2.3</arrayElem></arrayElem><arrayElem elemtype=\"java.lang.Double\" length=\"2\" type=\"[Ljava.lang.Double;\"><arrayElem type=\"java.lang.Double\">1.29</arrayElem><arrayElem type=\"java.lang.Double\">4.4</arrayElem></arrayElem></ArrayWithDoubles><ArrrayWithChars elemtype=\"[C\" length=\"2\" type=\"[[C\"><arrayElem elemtype=\"char\" length=\"2\" type=\"[C\"><arrayElem type=\"java.lang.Character\">e</arrayElem><arrayElem type=\"java.lang.Character\">d</arrayElem></arrayElem><arrayElem elemtype=\"char\" length=\"2\" type=\"[C\"><arrayElem type=\"java.lang.Character\">s</arrayElem><arrayElem type=\"java.lang.Character\">q</arrayElem></arrayElem></ArrrayWithChars><fieldWithChar>s</fieldWithChar><ListOfTestSub type=\"java.util.ArrayList\"><element type=\"TestSub\"><Int>14</Int></element><element type=\"TestSub\"><Int>2345</Int></element></ListOfTestSub><MapWithStringMappedOnList type=\"java.util.HashMap\"><entry type=\"java.util.HashMap$Node\"><key type=\"java.lang.String\">firstEntry</key><value type=\"java.util.ArrayList\"><element type=\"TestSub\"><Int>14</Int></element><element type=\"TestSub\"><Int>2345</Int></element></value></entry><entry type=\"java.util.HashMap$Node\"><key type=\"java.lang.String\">secondEntry</key><value type=\"java.util.ArrayList\"><element type=\"TestSub\"><Int>14</Int></element><element type=\"TestSub\"><Int>2345</Int></element></value></entry></MapWithStringMappedOnList></Test>";
        assertEquals(rightResult, fileContent);
        System.out.println("parameters: Class Test that contains another class, arrays and maps with list field; Result: Ok");
    }
}
