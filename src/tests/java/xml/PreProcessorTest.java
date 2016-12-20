package tests.java.xml;

import org.junit.Test;
import static org.junit.Assert.*;

public class PreProcessorTest {

    @Test
    public void testCheckAnnotationsValues() {
        System.out.println("ANNOTATIONS VALIDATOR TEST �1");
        System.out.println("checking no root element case");
        boolean expResult = false;
        boolean result = PreProcessor.checkAnnotationsValues(TestClassWithNoRootAnno.class);
        assertEquals(expResult, result);
        System.out.println("parameters: Class with no @TRootElement Annotation; Result: Ok");

    }

    @Test
    public void testCheckAnnotationsEqualValues() {
        System.out.println("ANNOTATIONS VALIDATOR TEST �2");
        System.out.println("checking equal values of annotations case");
        boolean expResult = false;
        boolean result = PreProcessor.checkAnnotationsValues(TestClassWithEqualAnnoValues.class);
        assertEquals(expResult, result);
        System.out.println("parameters: Class with equal @TElement Annotation values; Result: Ok");

    }

}
