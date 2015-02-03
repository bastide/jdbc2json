package com.github.bastide.jdbc2json.config;

import com.github.bastide.jdbc2json.config.Jdbc2jsonParser;
import java.io.InputStream;
import com.github.bastide.jdbc2json.Jdbc2JsonConfig;
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
public class Jdbc2jsonParserTest {
	private static final String GOOD_CONFIG = "sampleJdbc2jsonConfigurationFile.xml";
	
	public Jdbc2jsonParserTest() {
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

	/**
	 * Test of parse method, of class Jdbc2jsonParser.
	 * @throws java.lang.Exception
	 */
	@Test
	public void testParse_InputSource() throws Exception {
                //assertEquals(".", System.getProperty("user.dir"));
		Jdbc2JsonConfig config = new Jdbc2JsonConfig();
		Jdbc2jsonParser instance = new Jdbc2jsonParser(null, config);
		InputStream s = getClass().getResourceAsStream(GOOD_CONFIG);
		//InputStream s = new FileInputStream(GOOD_CONFIG);
                instance.parse(s);
		assertEquals(null, config.getConfigError());
		assertEquals("jdbc/Hello", config.getDataSource());
		assertEquals(1, config.getQueries().size());
		assertEquals(1, config.getTemplates().size());
		assertEquals( "<b>Hello, World!</b>", config.getTemplates().get("htmlFragment").trim());
	}

	
}
