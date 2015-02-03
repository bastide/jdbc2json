/*
 * All the configuration data that is extracted from the configuration file
 */

package com.github.bastide.jdbc2json;

import java.util.HashMap;
import java.util.Map;

public class Jdbc2JsonConfig {
	// default provided in the DTD
	private char ordinalParameterPrefix;
	private char reservedParameterPrefix;
	private char namedSQLParameterPrefix;
	private String driverString;
	private String dataSource;
	private String configError;
	private final Map<String, String> queries = new HashMap<>();
	private final Map<String, String> templates = new HashMap<>();

	public String getConfigError() {
		return configError;
	}

	public void setConfigError(String configError) {
		this.configError = configError;
	}

	public char getOrdinalParameterPrefix() {
		return ordinalParameterPrefix;
	}

	public void setOrdinalParameterPrefix(char ordinalParameterPrefix) {
		this.ordinalParameterPrefix = ordinalParameterPrefix;
	}

	public char getReservedParameterPrefix() {
		return reservedParameterPrefix;
	}

	public void setReservedParameterPrefix(char reservedParameterPrefix) {
		this.reservedParameterPrefix = reservedParameterPrefix;
	}

	public char getNamedSQLParameterPrefix() {
		return namedSQLParameterPrefix;
	}

	public void setNamedSQLParameterPrefix(char namedSQLParameterPrefix) {
		this.namedSQLParameterPrefix = namedSQLParameterPrefix;
	}

	public String getDriverString() {
		return driverString;
	}

	public void setDriverString(String driverString) {
		this.driverString = driverString;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	
	public void addQuery(String name, String sql) {
		queries.put(name, sql);
	}
	
	public Map<String, String> getQueries() {
		return queries;
	}

	public Map<String, String> getTemplates() {
		return templates;
	}

	public void addTemplate(String name, String code) {
		templates.put(name, code);
	}
	
}
