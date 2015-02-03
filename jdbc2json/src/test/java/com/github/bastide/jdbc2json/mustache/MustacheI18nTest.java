package com.github.bastide.jdbc2json.mustache;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author rbastide
 */
public class MustacheI18nTest {
    
    public MustacheI18nTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }


    @Test
    public void simpleTest() {
        String message = "Iñtërnâtiônàlizætiøn";
        String code= "Hello {{message}}";
        Template template = Mustache.compiler().compile(code);
        Map<String, String> data = new HashMap<>();
        data.put("message", message);
        String result = template.execute(data);
        assertEquals("Hello Iñtërnâtiônàlizætiøn", result);
        
    }
}
