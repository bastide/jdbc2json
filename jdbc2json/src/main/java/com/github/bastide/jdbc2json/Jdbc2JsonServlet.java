package com.github.bastide.jdbc2json;
import javax.servlet.annotation.WebServlet;

import com.github.bastide.jdbc2json.mustache.JSONTemplateProcessor;

/**
 * Processes JDBC requests using Mustache template engine
 * @author rbastide
 */
@WebServlet(
        name = "jdbc2json",
        description = "Sends JDBC resultSet as JSON",
        urlPatterns = "/jdbc2json"
)
public class Jdbc2JsonServlet extends QueryProcessorServlet {

	@Override
	protected ResultSetTemplateProcessor getResultSetProcessor() {
		return new JSONTemplateProcessor();
	}
}
