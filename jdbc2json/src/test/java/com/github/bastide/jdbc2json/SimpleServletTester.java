
package com.github.bastide.jdbc2json;

import com.github.bastide.jdbc2json.QueryProcessorServlet;
import com.github.bastide.jdbc2json.Jdbc2JsonServlet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.json.JSONObject;
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
public class SimpleServletTester {

	private static final String CONTEXT = "/context";
	private static final String[] initStatements = {
        "CREATE TABLE greeting (id IDENTITY, message VARCHAR(100) NOT NULL, createdOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",
		
	"CREATE PROCEDURE newGreeting(IN mess VARCHAR(100)) " +
	"MODIFIES SQL DATA DYNAMIC RESULT SETS 1 " +
	"BEGIN ATOMIC " +
	"DECLARE result CURSOR WITH RETURN FOR SELECT * FROM greeting WHERE id = IDENTITY(); " +
	"INSERT INTO greeting(message) VALUES (mess); " +
	"OPEN result; " +		
	"END",	
	
        //"INSERT INTO greeting(message) values ('hello'),('Iñtërnâtiônàlizætiøn'),('שלום ירושלים')"
	"INSERT INTO greeting(message) values ('hello'),('hi'),('Buenos dias')"
	};

	private Connection connection;

	ServletTester tester;

	public SimpleServletTester() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() throws Exception {
		connection = DriverManager.getConnection("jdbc:hsqldb:mem:testcase;shutdown=true,charset=utf-8", "sa", null);
		Statement stmt = connection.createStatement();
		for (String sql : initStatements) {
			stmt.execute(sql);
		}
		connection.commit();
		tester = new ServletTester();
		//tester.setContextPath(CONTEXT);
		tester.setResourceBase("src/test/resources/");
		ServletHolder holder = tester.addServlet(Jdbc2JsonServlet.class, "/jdbc2json/*");
		//holder.setName("jdbc2json");
		holder.setInitParameter(QueryProcessorServlet.CONFIGURATION_FILE_INIT_PARAMETER, QueryProcessorServlet.DEFAULT_CONFIGURATION_FILE);
                tester.start();
        }

	@After
	public void tearDown() throws SQLException, Exception {
                tester.stop();
                
		Statement stmt = connection.createStatement();
		stmt.execute("DROP PROCEDURE newGreeting");
		stmt.execute("DROP TABLE greeting");

		connection.close();
	}

	@Test
	public void testSimpleQuery() throws IOException, Exception {
		HttpTester request = new HttpTester();
		request.setMethod("GET");
		request.setHeader("Host", "tester");
		request.setURI("/jdbc2json/allGreetings");
		request.setVersion("HTTP/1.0");
		HttpTester response = new HttpTester();
                String rawRequest = request.generate();
                String rawResponse = tester.getResponses(rawRequest);
		response.parse(rawResponse);
		System.err.println(response.getContent());
		JSONObject parsedResponse = new JSONObject(response.getContent());
		assertEquals(3, parsedResponse.getJSONArray("records").length());
		assertEquals("application/json; charset=UTF-8", response.getHeader("Content-Type"));
		assertEquals(200, response.getStatus());
	}

	@Test
	public void testParameterizedQuery() throws IOException, Exception {
		HttpTester request = new HttpTester();
		request.setMethod("GET");
		request.setHeader("Host", "tester");
		request.setURI("/jdbc2json/greetingById?id=0");
		request.setVersion("HTTP/1.0");
		HttpTester response = new HttpTester();
		response.parse(tester.getResponses(request.generate()));
		System.out.println(response.getContent());
		JSONObject parsedResponse = new JSONObject(response.getContent());
		assertEquals("hello", parsedResponse.getJSONArray("records").getJSONObject(0).getString("MESSAGE"));
	}
	
	@Test
	public void testStoredProcedure() throws IOException, Exception {
		HttpTester request = new HttpTester();
		request.setMethod("GET");
		request.setHeader("Host", "tester");
		request.setURI("/jdbc2json/addGreeting?message=testing");
		request.setVersion("HTTP/1.0");
		HttpTester response = new HttpTester();
		response.parse(tester.getResponses(request.generate()));
		System.out.println(response.getContent());
		JSONObject parsedResponse = new JSONObject(response.getContent());
		assertEquals("hello", parsedResponse.getJSONArray("records").getJSONObject(0).getString("MESSAGE"));
	}	

}
