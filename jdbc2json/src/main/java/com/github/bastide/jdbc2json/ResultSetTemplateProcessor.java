package com.github.bastide.jdbc2json;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Set;

/**
 *
 * @author bastide
 */
public interface ResultSetTemplateProcessor {
	public void init() ;
	public String getContentType(String templateName, IterableResultSet rs);		
	public void processTemplate(String templateName, IterableResultSet rs, PrintWriter out) throws SQLException;	
        public Set<String> getTemplateNames();
}
