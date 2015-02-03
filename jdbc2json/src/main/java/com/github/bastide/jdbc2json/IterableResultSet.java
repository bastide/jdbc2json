
package com.github.bastide.jdbc2json;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 *
 * @author bastide
 */
public class IterableResultSet {

	final int updateCount;

	public ResultSetColumnMetaData[] getMetaData() {
		return metaData;
	}

	public Iterator<ResultSetRecord> getRecords() {
		return records;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public int getUpdateCount() {
		return updateCount;
	}
	
	final ResultSet resultSet;
	Iterator<ResultSetRecord> records;
	ResultSetColumnMetaData[] metaData;

	public IterableResultSet(ResultSet rs, int updateCount) throws SQLException {
		resultSet = rs;
		this.updateCount = updateCount;
		int numCols;
		if (resultSet == null) {
			numCols = 0;
		} else {
			numCols = resultSet.getMetaData().getColumnCount();
		}
		metaData = new ResultSetColumnMetaData[numCols];
		for (int currentCol = 0; currentCol < numCols; currentCol++) {
			metaData[currentCol] = new ResultSetColumnMetaData(resultSet.getMetaData(), currentCol + 1);
		}
		records = new Iterator<ResultSetRecord>() {

			Boolean lastNextResult = null;

			@Override
			public boolean hasNext() {
				if (null == resultSet)
					return false;
				try {
					if (lastNextResult == null) {
						lastNextResult = resultSet.next();
					}
					return lastNextResult;
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public ResultSetRecord next() {
				try {
					lastNextResult = null;
					return new ResultSetRecord(resultSet, metaData);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public void close() throws SQLException {
		if (resultSet != null)
			resultSet.close();
	}
}
