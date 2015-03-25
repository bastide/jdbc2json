package com.github.bastide.jdbc2json;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.StringCharacterIterator;

/**
 *
 * @author bastide
 */
class ResultSetRecord {

	final ResultSetColumn[] columns;
	final ResultSetColumnMetaData[] metaData;

	ResultSetRecord(ResultSet rs, ResultSetColumnMetaData[] metaData) throws SQLException {
		this.metaData = metaData;
		columns = new ResultSetColumn[metaData.length];
		for (int currentCol = 1; currentCol <= metaData.length; currentCol++) {
			columns[currentCol - 1] = new ResultSetColumn(metaData[currentCol - 1], getValue(rs, currentCol));
		}

	}

	protected Object getValue(ResultSet rs, int i) throws SQLException {
		Object result;
		if (null == rs.getObject(i)) {
			return "null";
		}

		switch (metaData[i - 1].type) {
			case java.sql.Types.NVARCHAR:
			case java.sql.Types.VARCHAR:
			case java.sql.Types.CHAR:
				result = "\"" + forJSON(rs.getString(i)) + "\"";
				break;

			case java.sql.Types.DATE:
				result = "\"" + rs.getDate(i) + "\"";
//				result = "new Date(" + rs.getDate(i).getTime() + ")";
				break;

			case java.sql.Types.TIMESTAMP:
				result = "\"" + rs.getTimestamp(i) + "\"";
//				result = "new Date(" + rs.getTimestamp(i).getTime() + ")";
				break;

			default:
				result = rs.getObject(i);
				break;
		}
		return result;
	}

	public static String forJSON(String aText) {
		final StringBuilder result = new StringBuilder();
		StringCharacterIterator iterator = new StringCharacterIterator(aText);
		char character = iterator.current();
		while (character != StringCharacterIterator.DONE) {
			if (character == '\"') {
				result.append("\\\"");
			} else if (character == '\\') {
				result.append("\\\\");
			} else if (character == '/') {
				result.append("\\/");
			} else if (character == '\b') {
				result.append("\\b");
			} else if (character == '\f') {
				result.append("\\f");
			} else if (character == '\n') {
				result.append("\\n");
			} else if (character == '\r') {
				result.append("\\r");
			} else if (character == '\t') {
				result.append("\\t");
			} else {
				//the char is not a special one
				//add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}
}