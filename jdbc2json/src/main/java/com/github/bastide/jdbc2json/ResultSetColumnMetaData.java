
package com.github.bastide.jdbc2json;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 *
 * @author bastide
 */
class ResultSetColumnMetaData {

	final int type;
	final int displaySize;
	final String label;
	final String name;
	final String typeName;
	final int precision;
	final int scale;

	ResultSetColumnMetaData(ResultSetMetaData metaData, int column) throws SQLException {
		type = metaData.getColumnType(column);
		displaySize = metaData.getColumnDisplaySize(column);
		label = metaData.getColumnLabel(column);
		name = metaData.getColumnName(column);
		typeName = metaData.getColumnTypeName(column);
		precision = metaData.getPrecision(column);
		scale = metaData.getScale(column);
	}
}