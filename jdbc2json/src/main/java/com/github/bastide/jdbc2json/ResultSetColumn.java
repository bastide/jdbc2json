
package com.github.bastide.jdbc2json;

import java.sql.SQLException;

/**
 *
 * @author bastide
 */
class ResultSetColumn {

	final ResultSetColumnMetaData metaData;
	final Object value;

	ResultSetColumn(ResultSetColumnMetaData metaData, Object value) throws SQLException {
		this.value = value;
		this.metaData = metaData;
	}
}