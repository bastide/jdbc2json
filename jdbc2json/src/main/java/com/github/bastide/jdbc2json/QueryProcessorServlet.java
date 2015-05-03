package com.github.bastide.jdbc2json;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import com.github.bastide.jdbc2json.config.Jdbc2jsonParser;
import org.xml.sax.SAXException;

/**
 *
 * @author rbastide
 */
public abstract class QueryProcessorServlet extends HttpServlet {

	public static final String JSON_CONTENT_TYPE = "application/json; charset=UTF-8";
	protected static final String NO_DRIVER_STRING_OR_DATA_SOURCE_CONFIGURED = "no driverString or dataSource configured in web.xml";
	public static final String TEMPLATE_PARAMETER = "template";
	public static final String CONFIGURATION_FILE_INIT_PARAMETER = "configurationFile";
	public static final String DEFAULT_CONFIGURATION_FILE = "jdbc2json.xml";
	private ResultSetTemplateProcessor templateProcessor;
	protected DataSource dataSource;
	protected Jdbc2JsonConfig config;
	/**
	 * The logger is created and named in init, according to the servlet's name
	 */
	@SuppressWarnings("NonConstantLogger")
	protected Logger logger;
	protected String servletName;

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		Connection connection = null;
		QueryStatement statement = null;
		IterableResultSet rs = null;
		//response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		try {
			// Report config errors (detected at init()) to the client
			if (null != config.getConfigError()) {
				throw new Exception(config.getConfigError());
			}
			connection = getConnection(request);
			String query = findSQLQuery(request);
			statement = createStatement(connection, query);
			statement.setParametersFromRequest(request);

			rs = statement.getResultSet();
			String templateName = request.getParameter(TEMPLATE_PARAMETER);
			try {
				String contentType = templateProcessor.getContentType(templateName, rs);
				response.setContentType(contentType);
			} catch (NullPointerException e) {
				throw new Exception(String.format("Template %s unknown", templateName));
			}
			templateProcessor.processTemplate(templateName, rs, response.getWriter());
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
			response.setContentType("text/plain; charset=utf-8");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(e.getMessage());
			//response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException ex) {
				}
			}
			if (null != statement) {
				try {
					statement.close();
				} catch (SQLException ex) {
				}
			}
			if (null != connection) {
				try {
					connection.close();
				} catch (SQLException ex) {
				}
			}
		}
	}

	protected QueryStatement createStatement(Connection connection, String query) throws SQLException {
		return new NamedParameterStatement(connection, query);
	}

	/**
	 * Open a connection to the database, using datasource or driverstring
	 *
	 * @param request
	 * @return a connection to te database
	 * @throws Exception if no connection can be found
	 */
	protected Connection getConnection(HttpServletRequest request) throws Exception {
		// either datasource or driverString should be found in init()
		if (null != dataSource) {
			return dataSource.getConnection();
		}

		if (null != config.getDriverString()) {
			return DriverManager.getConnection(config.getDriverString());
		}
		// Should never get there, handled in init()
		throw new Exception(NO_DRIVER_STRING_OR_DATA_SOURCE_CONFIGURED);
	}

	protected String findSQLQuery(HttpServletRequest request) throws Exception {
		String queryName = request.getPathInfo();
		// Maybe we are included in a JSP
		if (queryName==null) {
		    Object includePathInfo = request.getAttribute("javax.servlet.include.path_info");
		    if (includePathInfo != null)
			queryName = includePathInfo.toString();
		}
		if (queryName == null || "/".equals(queryName) ) {
			throw new Exception("No query name found in URI: " + request.getRequestURI());
		} else {
			// remove leading /
			queryName = queryName.substring(1, queryName.length());
			String query = config.getQueries().get(queryName);
			if (query == null) {
				throw new Exception("SQL Query '" + queryName + "' not found in configuration");
			}
			return query;
		}
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		config = new Jdbc2JsonConfig();

		servletName = servletConfig.getServletName();
		logger = Logger.getLogger(servletName);
		logger.log(Level.CONFIG, "Servlet {0} starting init", servletName);
		// Find configuration file
		String configFile = servletConfig.getInitParameter(CONFIGURATION_FILE_INIT_PARAMETER);
		if (configFile == null) {
			configFile = servletName + ".xml";
		}
		// Search alingside web.xml
		configFile = "WEB-INF/" + configFile;
		InputStream configStream = getServletContext().getResourceAsStream("/" + configFile);
		if (null != configStream) {
			try {
				new Jdbc2jsonParser(logger, config).parse(configStream);
				// Find driverString or DataSource
				String datasourceJNDIName = config.getDataSource();
				if (null != datasourceJNDIName) {
					Context envCtx = (Context) new InitialContext().lookup("java:comp/env");
					dataSource = (DataSource) envCtx.lookup(datasourceJNDIName);
					logger.log(Level.INFO, "Servlet {0}  is using datasource {1} ", new Object[]{servletName, datasourceJNDIName});
				}
				logger.log(Level.INFO, "Servlet {0} found {1} queries in {2}", new Object[]{servletName, config.getQueries().size(), configFile});

				// init template templateProcessor
				templateProcessor = getResultSetProcessor();
				templateProcessor.init();
			} catch (ParserConfigurationException | IOException | SAXException | NamingException ex) {
				String file = getServletContext().getRealPath(configFile);
				String message = String.format("Configuration error in %s: %s",
					file,
					ex.getMessage()
				);
				logger.log(Level.SEVERE, message);
				config.setConfigError(message);
			}
		} else {
			String message = String.format("Could not locate configuration file %s", servletConfig.getServletContext().getRealPath("/") + configFile);
			logger.log(Level.SEVERE, message);
			config.setConfigError(message);
		}

	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "ResultSetToJSON Servlet";
	}// </editor-fold>

	protected abstract ResultSetTemplateProcessor getResultSetProcessor();
}
