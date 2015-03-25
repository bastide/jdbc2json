package com.github.bastide.jdbc2json;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class IndexMap extends HashMap<String, List<Integer>> {
};

/**
 *
 * @author adam_crume
 */
class NamedParameterStatement {

	/**
	 * The statement this object is wrapping.
	 */
	private final PreparedStatement statement;
	/**
	 * Maps parameter names to arrays of ints which are the parameter indices.
	 */
	private final IndexMap indexMap;

	/**
	 * Creates a NamedParameterStatement. Wraps a call to * c.{@link Connection#prepareStatement(java.lang.String)
	 * prepareStatement}.
	 *
	 * @param connection the database connection
	 * @param query the parameterized query
	 * @throws SQLException if the statement could not be created
	 */
	public NamedParameterStatement(Connection connection, String query) throws SQLException {
		indexMap = new IndexMap();
		String parsedQuery = parse(query, indexMap);
		statement = connection.prepareStatement(parsedQuery, Statement.RETURN_GENERATED_KEYS);
	}

	/**
	 * Parses a query with named parameters. The parameter-index mappings are put into the map, and the parsed query is
	 * returned. DO NOT CALL FROM CLIENT CODE. This method is non-private so JUnit code can test it.
	 *
	 * @param query query to parse
	 * @param paramMap map to hold parameter-index mappings
	 * @return the parsed query
	 */
	static String parse(String query, IndexMap paramMap) {
		// I was originally using regular expressions, but they didn't work well for ignoring
		// parameter-like strings inside quotes.
		int length = query.length();
		StringBuilder parsedQuery = new StringBuilder(length);
		boolean inSingleQuote = false;
		boolean inDoubleQuote = false;
		int index = 1;

		for (int i = 0; i < length; i++) {
			char c = query.charAt(i);
			if (inSingleQuote) {
				if (c == '\'') {
					inSingleQuote = false;
				}
			} else if (inDoubleQuote) {
				if (c == '"') {
					inDoubleQuote = false;
				}
			} else {
				if (c == '\'') {
					inSingleQuote = true;
				} else if (c == '"') {
					inDoubleQuote = true;
				} else if (c == ':' && i + 1 < length
					&& Character.isJavaIdentifierStart(query.charAt(i + 1))) {
					int j = i + 2;
					while (j < length && Character.isJavaIdentifierPart(query.charAt(j))) {
						j++;
					}
					String name = query.substring(i + 1, j);
					c = '?'; // replace the parameter with a question mark
					i += name.length(); // skip past the end if the parameter

					List<Integer> indexList = paramMap.get(name);
					if (indexList == null) {
						indexList = new LinkedList<>();
						paramMap.put(name, indexList);
					}
					indexList.add(index);

					index++;
				}
			}
			parsedQuery.append(c);
		}

		return parsedQuery.toString();
	}

	/**
	 * Returns the indexes for a parameter.
	 *
	 * @param name parameter name
	 * @return parameter indexes
	 * @throws IllegalArgumentException if the parameter does not exist
	 */
	private List<Integer> getIndexes(String name) {
		List<Integer> indexes = indexMap.get(name);
		if (indexes == null) {
			indexes = Collections.emptyList();
			// TODO : log ?
		}
		return indexes;
	}

	/**
	 * @param parameters
	 * @throws java.sql.SQLException
	 * @see http://docs.oracle.com/javase/1.5.0/docs/guide/jdbc/getstart/mapping.html
	 *
	 */
	public void setParametersFromRequest(Map<String, String[]> parameters) throws SQLException, Exception {
		ParameterMetaData queryMetaData = statement.getParameterMetaData();
                // TODO : is it a good idea to null everything ?
                for(int index = 1; index <= queryMetaData.getParameterCount(); index++)
                    if (queryMetaData.isNullable(index) == ParameterMetaData.parameterNullable)
                        statement.setNull(index, queryMetaData.getParameterType(index));
                        
                            
		for (String parameterName : parameters.keySet()) {
			List<Integer> indices = getIndexes(parameterName);
			String[] values = parameters.get(parameterName);
			if (values.length > 0) {
				String parameterValue = values[0]; // TODO : what about the others ?
				for (int parameterIndex : indices) {
					int parameterType = queryMetaData.getParameterType(parameterIndex);
					try {
						switch (parameterType) {

							case java.sql.Types.BIGINT:
								statement.setLong(parameterIndex, Long.parseLong(parameterValue));
								break;

							case java.sql.Types.INTEGER:
								statement.setInt(parameterIndex, Integer.parseInt(parameterValue));
								break;

							case java.sql.Types.BOOLEAN:
								statement.setBoolean(parameterIndex, Boolean.parseBoolean(parameterValue));
								break;

							case java.sql.Types.ARRAY:
								break;
							case java.sql.Types.BLOB:
								break;

							case java.sql.Types.DOUBLE:
							case java.sql.Types.DECIMAL:
							case java.sql.Types.NUMERIC:
								statement.setBigDecimal(parameterIndex, new BigDecimal(parameterValue));
								break;

							case java.sql.Types.FLOAT:
								statement.setDouble(parameterIndex, Double.parseDouble(parameterValue));
								break;

							case java.sql.Types.REAL:
								statement.setFloat(parameterIndex, Float.parseFloat(parameterValue));
								break;

							case java.sql.Types.NVARCHAR:
							case java.sql.Types.VARCHAR:
							case java.sql.Types.CHAR:
							case java.sql.Types.LONGVARCHAR:
								statement.setString(parameterIndex, parameterValue);
								break;

							case java.sql.Types.TINYINT:
								statement.setByte(parameterIndex, Byte.parseByte(parameterValue));
								break;

							case java.sql.Types.SMALLINT:
								statement.setShort(parameterIndex, Short.parseShort(parameterValue));
								break;

							case java.sql.Types.TIMESTAMP:
								statement.setTimestamp(parameterIndex, Timestamp.valueOf(parameterValue));
								break;

							case java.sql.Types.TIME:
								statement.setTime(parameterIndex, Time.valueOf(parameterValue));
								break;

							case java.sql.Types.DATE:
								statement.setDate(parameterIndex, Date.valueOf(parameterValue));
								break;

							default:
								statement.setObject(parameterIndex, parameterValue);
								break;
						}
					} catch (NumberFormatException e) {
						throw new Exception("Incorrect value: " + parameterValue + " for parameter " + parameterName);
					}
				}
			}
		}

	}

	protected IterableResultSet getResultSet() throws SQLException {
		ResultSet rs;
		boolean hasResultSet = statement.execute();
		if (hasResultSet) {
			rs = statement.getResultSet();
		} else {
			rs = statement.getGeneratedKeys();
		}
		return new IterableResultSet(rs, statement.getUpdateCount());
	}

	/**
	 * Returns the underlying statement.
	 *
	 * @return the statement
	 */
	public PreparedStatement getStatement() {
		return statement;
	}

	/**
	 * Executes the statement.
	 *
	 * @return true if the first result is a {@link ResultSet}
	 * @throws SQLException if an error occurred
	 * @see PreparedStatement#execute()
	 */
	public boolean execute() throws SQLException {
		return statement.execute();
	}

	/**
	 * Executes the statement, which must be a query.
	 *
	 * @return the query results
	 * @throws SQLException if an error occurred
	 * @see PreparedStatement#executeQuery()
	 */
	public ResultSet executeQuery() throws SQLException {
		return statement.executeQuery();
	}

	/**
	 * Executes the statement, which must be an SQL INSERT, UPDATE or DELETE statement; or an SQL statement that returns
	 * nothing, such as a DDL statement.
	 *
	 * @return number of rows affected
	 * @throws SQLException if an error occurred
	 * @see PreparedStatement#executeUpdate()
	 */
	public int executeUpdate() throws SQLException {
		return statement.executeUpdate();
	}

	/**
	 * Closes the statement.
	 *
	 * @throws SQLException if an error occurred
	 * @see Statement#close()
	 */
	public void close() throws SQLException {
		statement.close();
	}

	/**
	 * Adds the current set of parameters as a batch entry.
	 *
	 * @throws SQLException if something went wrong
	 */
	public void addBatch() throws SQLException {
		statement.addBatch();
	}

	/**
	 * Executes all of the batched statements.
	 *
	 * See {@link Statement#executeBatch()} for details.
	 *
	 * @return update counts for each statement
	 * @throws SQLException if something went wrong
	 */
	public int[] executeBatch() throws SQLException {
		return statement.executeBatch();
	}

}
