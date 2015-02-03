package com.github.bastide.jdbc2json.i18n;

import java.io.IOException;
import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class I18nServletTester {
	private ServletTester tester;

	@Before
	public void setUp() throws Exception {
		tester = new ServletTester();
		tester.addServlet(I18nServlet.class, "/i18n/*");
		tester.start();
	}

	@After
	public void tearDown() throws Exception {
            tester.stop();
        }

        @Test
	public void testI18n() throws IOException, Exception {
		HttpTester request = new HttpTester();
		request.setMethod("GET");
		request.setHeader("Host", "tester");
		request.setURI("/i18n");
		request.setVersion("HTTP/1.0");
		HttpTester response = new HttpTester();
		response.parse(tester.getResponses(request.generate()));
		String s = response.getContent();
		assertEquals(200, response.getStatus());
		assertEquals("UTF-8", response.getCharacterEncoding());
		assertEquals("text/plain;charset=UTF-8", response.getHeader("Content-Type"));
		assertEquals("Iñtërnâtiônàlizætiøn", s);
	}    
}
