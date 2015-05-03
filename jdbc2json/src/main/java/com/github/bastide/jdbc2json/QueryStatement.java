package com.github.bastide.jdbc2json;

import java.sql.SQLException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author rbastide
 */
public interface QueryStatement {

    IterableResultSet getResultSet() throws SQLException;

    /**
     * @param request
     * @throws java.sql.SQLException
     * @see http://docs.oracle.com/javase/1.5.0/docs/guide/jdbc/getstart/mapping.html
     *
     */
    void setParametersFromRequest(HttpServletRequest request) throws SQLException, Exception;
    
    public void close() throws SQLException;
    
}
