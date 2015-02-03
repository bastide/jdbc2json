package com.github.bastide.jdbc2json;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author bastide
 */
public class JsonMetaDataServlet extends Jdbc2JsonServlet {

	@Override
	protected NamedParameterStatement createStatement(Connection connection, String query) throws SQLException {
		// No need for a atatement
		return null;
	}

// TODO:	@Override
	protected IterableResultSet getResultSet(HttpServletRequest request, Connection c, PreparedStatement statement) throws SQLException {
		DatabaseMetaData metaData = c.getMetaData();
		String metaDataPropertyName = request.getPathInfo();
		ResultSet metaDataProperty;
		switch (metaDataPropertyName) {
			case "/bestRowIdentifier":
				metaDataProperty = metaData.getBestRowIdentifier(
					request.getParameter("catalog"),
					request.getParameter("schema"),
					request.getParameter("table"),
					request.getParameter("scope") == null ? DatabaseMetaData.bestRowTransaction : Integer.parseInt(request.getParameter("scope")),
					request.getParameter("nullable") == null ? true : Boolean.parseBoolean(request.getParameter("nullable")));
				break;
			case "/catalogs":
				metaDataProperty = metaData.getCatalogs();
				break;
			case "/clientInfoProperties":
				metaDataProperty = metaData.getClientInfoProperties();
				break;
			case "/columnPrivileges":
				metaDataProperty = metaData.getColumnPrivileges(
					request.getParameter("catalog"),
					request.getParameter("schema"),
					request.getParameter("table"),
					request.getParameter("columnNamePattern"));
				break;
			case "/columns":
				metaDataProperty = metaData.getColumns(
					request.getParameter("catalog"),
					request.getParameter("schema"),
					request.getParameter("table"),
					request.getParameter("columnNamePattern"));
				break;
			case "/crossReference":
				metaDataProperty = metaData.getCrossReference(
					request.getParameter("parentCatalog"),
					request.getParameter("parentSchema"),
					request.getParameter("parentTable"),
					request.getParameter("foreignCatalog"),
					request.getParameter("foreignSchema"),
					request.getParameter("foreignTable"));
				break;
			case "/exportedKeys":
				metaDataProperty = metaData.getExportedKeys(
					request.getParameter("catalog"),
					request.getParameter("schema"),
					request.getParameter("table"));
				break;
			case "/functionColumns":
				metaDataProperty = metaData.getFunctionColumns(
					request.getParameter("catalog"),
					request.getParameter("schemaPattern"),
					request.getParameter("functionNamePattern"),
					request.getParameter("columnNamePattern"));
				break;
			case "/functions":
				metaDataProperty = metaData.getFunctions(
					request.getParameter("catalog"),
					request.getParameter("schemaPattern"),
					request.getParameter("functionNamePattern"));
				break;
			case "/importedKeys":
				metaDataProperty = metaData.getImportedKeys(
					request.getParameter("catalog"),
					request.getParameter("schema"),
					request.getParameter("table"));
				break;
			case "/indexInfo":
				metaDataProperty = metaData.getIndexInfo(
					request.getParameter("catalog"),
					request.getParameter("schema"),
					request.getParameter("table"),
					request.getParameter("unique") == null ? false : Boolean.parseBoolean(request.getParameter("unique")),
					request.getParameter("approximate") == null ? false : Boolean.parseBoolean(request.getParameter("approximate")));

				break;
			case "/primaryKeys":
				metaDataProperty = metaData.getPrimaryKeys(
					request.getParameter("catalog"),
					request.getParameter("schema"),
					request.getParameter("table"));
				break;
			case "/procedureColumns":
				metaDataProperty = metaData.getProcedureColumns(
					request.getParameter("catalog"),
					request.getParameter("schemaPattern"),
					request.getParameter("procedureNamePattern"),
					request.getParameter("columnNamePattern"));
				break;
			case "/procedures":
				metaDataProperty = metaData.getProcedures(
					request.getParameter("catalog"),
					request.getParameter("schemaPattern"),
					request.getParameter("procedureNamePattern"));
				break;
			case "/schemas":
				metaDataProperty = metaData.getSchemas(
					request.getParameter("catalog"),
					request.getParameter("schemaPattern"));
				break;
			case "/superTables":
				metaDataProperty = metaData.getSuperTables(
					request.getParameter("catalog"),
					request.getParameter("schemaPattern"),
					request.getParameter("tableNamePattern"));
				break;
			case "/superTypes":
				metaDataProperty = metaData.getSuperTypes(
					request.getParameter("catalog"),
					request.getParameter("schemaPattern"),
					request.getParameter("typeNamePattern"));
				break;
			case "/tablePrivileges":
				metaDataProperty = metaData.getTablePrivileges(
					request.getParameter("catalog"),
					request.getParameter("schemaPattern"),
					request.getParameter("tableNamePattern"));
				break;
			case "/tableTypes":
				metaDataProperty = metaData.getTableTypes();
				break;
			case "/typeInfo":
				metaDataProperty = metaData.getTypeInfo();
				break;
			case "/UDTs":
				metaDataProperty = metaData.getUDTs(
					request.getParameter("catalog"),
					request.getParameter("schemaPattern"),
					request.getParameter("typeNamePattern"),
					null);
				break;
			case "/versionColumns":
				metaDataProperty = metaData.getVersionColumns(
					request.getParameter("catalog"),
					request.getParameter("schema"),
					request.getParameter("table"));
				break;
			case "/tables":
			default:
				metaDataProperty = metaData.getTables(
					request.getParameter("catalog"),
					request.getParameter("schemaPattern"),
					request.getParameter("tableNamePattern"),
					request.getParameterValues("types"));
				break;

		}
		return new IterableResultSet(metaDataProperty, -1);
	}
}
