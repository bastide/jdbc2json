/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.bastide.jdbc2json.mustache;

import com.github.bastide.jdbc2json.ResultSetTemplate;
import com.samskivert.mustache.Template;

/**
 *
 * @author rbastide
 */
public class MustacheResultSetTemplate extends ResultSetTemplate {

	private Template compiledTemplate;

	/**
	 * Get the value of compiledTemplate
	 *
	 * @return the value of compiledTemplate
	 */
	public Template getCompiledTemplate() {
		return compiledTemplate;
	}

	/**
	 * Set the value of compiledTemplate
	 *
	 * @param compiledTemplate new value of compiledTemplate
	 */
	public void setCompiledTemplate(Template compiledTemplate) {
		this.compiledTemplate = compiledTemplate;
	}

	public MustacheResultSetTemplate(String code, String contentType) {
		super(code, contentType);
	}
}
