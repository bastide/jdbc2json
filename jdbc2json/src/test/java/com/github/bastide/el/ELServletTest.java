package com.github.bastide.el;

import java.io.IOException;
import java.sql.SQLException;
import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.junit.After;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

/**
 *
 * @author rbastide
 */
public class ELServletTest {

	private HttpTester request, response;


	ServletTester tester;

	@Before
	public void setUp() throws Exception {
		Class.forName("org.apache.jasper.servlet.JspServlet");
		tester = new ServletTester();
		tester.addServlet(JspServlet.class, "*.jsp");
		ServletHolder holder = tester.addServlet(ElServlet.class, "/elservlet/*");
		tester.start();
	}
	
	private String getResponse(String URI) throws IOException, Exception {
		request = new HttpTester();
		request.setMethod("GET");
		request.setHeader("Host", "tester");
		request.setVersion("HTTP/1.0");
		response = new HttpTester();
		request.setURI(URI);
		String rawRequest = request.generate();
		String rawResponse = tester.getResponses(rawRequest);
		response.parse(rawResponse);
		return response.getContent();
	}

	@After
	public void tearDown() throws SQLException, Exception {
		tester.stop();
	}

	@Ignore("") @Test
	public void testSimpleQuery() throws IOException, Exception {
		String resp = getResponse("/elservlet?paramName=paramValue");
		//System.err.println(resp);
		assertEquals("hello", resp);
	}


}
