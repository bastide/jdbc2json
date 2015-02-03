package com.github.bastide.jdbc2json;

/**
 *
 * @author rbastide
 */
public class ResultSetTemplate {

	private final String code;
	private final String contentType;

	public ResultSetTemplate(String code, String contentType) {
		this.code = code;
		this.contentType = contentType;
	}

	/**
	 * Get the value of contentType
	 *
	 * @return the value of contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Get the value of code
	 *
	 * @return the value of code
	 */
	public String getCode() {
		return code;
	}
}
