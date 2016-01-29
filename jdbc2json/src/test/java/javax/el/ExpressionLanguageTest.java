package javax.el;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rbastide
 */
public class ExpressionLanguageTest {
        ELProcessor elp;


    public ExpressionLanguageTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
	elp = new ELProcessor();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void hello() {
	Object result = elp.eval("4+5");
	assertEquals(9L, result);
    
    }

}
