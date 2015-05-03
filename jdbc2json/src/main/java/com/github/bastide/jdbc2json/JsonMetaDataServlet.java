package com.github.bastide.jdbc2json;

import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author bastide
 */
@WebServlet(
        name = "jsonMetaData",
        description = "Explore metadata as JSON",
        urlPatterns = "/jsonMetaData/*"
)
public class JsonMetaDataServlet extends Jdbc2JsonServlet {

    @Override
    protected QueryStatement createStatement(Connection connection, String query) throws SQLException {
	return new MetaDataStatement(connection, query);
    }

    @Override
    protected String findSQLQuery(HttpServletRequest request) throws Exception {
	return request.getPathInfo();
    }

}
