package com.github.bastide.jdbc2json.mustache;

import com.github.bastide.jdbc2json.IterableResultSet;
import com.github.bastide.jdbc2json.QueryProcessorServlet;
import com.github.bastide.jdbc2json.ResultSetTemplateProcessor;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author bastide
 */
public class JSONTemplateProcessor implements ResultSetTemplateProcessor {

	private static final String DEFAULT_INSERT_TEMPLATE = "InsertQuery";
	private static final String DEFAULT_SELECT_TEMPLATE = "VerboseResult";
	private static final String DEFAULT_UPDATE_TEMPLATE = "UpdateQuery";
	private final Map<String, MustacheResultSetTemplate> templates = new HashMap<>();

	@Override
	public void init() {
		templates.put(
			DEFAULT_INSERT_TEMPLATE,
			new MustacheResultSetTemplate(
			"{\t\"updateCount\" : {{updateCount}},\n\t\"metaData\" :{{>MetaData}},\n\t\"insertedKeys\":{{>AllRecordsAsArray}}\n}",
			QueryProcessorServlet.JSON_CONTENT_TYPE));
		templates.put(
			DEFAULT_UPDATE_TEMPLATE,
			new MustacheResultSetTemplate(
			"{ \"updateCount\" : {{updateCount}} }",
			QueryProcessorServlet.JSON_CONTENT_TYPE));
		templates.put(
			"ColumnMetaData",
			new MustacheResultSetTemplate(
			"\t{ \"type\": {{type}}, \"displaySize\": {{displaySize}}, \"label\": \"{{label}}\", \"name\": \"{{name}}\", \"typeName\": \"{{typeName}}\", \"precision\": {{precision}}, \"scale\": {{scale}} }",
			QueryProcessorServlet.JSON_CONTENT_TYPE));
		templates.put(
			"MetaData",
			new MustacheResultSetTemplate(
			"[{{#metaData}}{{^-first}},{{/-first}}\n\t{{>ColumnMetaData}}{{/metaData}}\n\t]",
			QueryProcessorServlet.JSON_CONTENT_TYPE));
		templates.put("RecordAsObject",
			new MustacheResultSetTemplate(
			"{ {{#columns}}{{^-first}},{{/-first}}\n\t\t\"{{this.metaData.name}}\" : {{this.value}}{{/columns}}\n\t}",
			QueryProcessorServlet.JSON_CONTENT_TYPE));
		templates.put("RecordByKey",
			new MustacheResultSetTemplate(
			" {{#columns}}{{#-first}}\t{{this.value}} : { {{/-first}} {{^-first}},{{/-first}}\n\t\t\t\"{{this.metaData.name}}\" : {{this.value}}{{/columns}}\n\t\t}",
			QueryProcessorServlet.JSON_CONTENT_TYPE));
		templates.put(
			"RecordAsArray",
			new MustacheResultSetTemplate(
			"\t{ \"columns\" : [{{#columns}}{{^-first}}, {{/-first}} {{this.value}} {{/columns}}]}",
			QueryProcessorServlet.JSON_CONTENT_TYPE));
		templates.put(
			"AllRecordsAsArray",
			new MustacheResultSetTemplate(
			"[{{#records}}{{^-first}},{{/-first}}\n\t{{>RecordAsArray}}{{/records}}\n\t]",
			QueryProcessorServlet.JSON_CONTENT_TYPE));
		templates.put(
			"AllRecordsAsObjects",
			new MustacheResultSetTemplate(
			"[{{#records}}{{^-first}},{{/-first}}\n\t{{>RecordAsObject}}{{/records}}\n\t]",
			QueryProcessorServlet.JSON_CONTENT_TYPE));
		templates.put(
			"FullResult",
			new MustacheResultSetTemplate(
			"{\t\"metaData\" :{{>MetaData}},\n\t\"records\":{{>AllRecordsAsArray}}\n}",
			QueryProcessorServlet.JSON_CONTENT_TYPE));
		templates.put(
			"AllRecordsByKey",
			new MustacheResultSetTemplate(
			"{ {{#records}}{{^-first}},{{/-first}}\n\t{{>RecordByKey}}{{/records}}\n\t}",
			QueryProcessorServlet.JSON_CONTENT_TYPE));
		templates.put(
			"KeyedResult",
			new MustacheResultSetTemplate(
			"{\t\"metaData\" :{{>MetaData}},\n\t\"records\":{{>AllRecordsByKey}}\n}",
			QueryProcessorServlet.JSON_CONTENT_TYPE));
		templates.put(
			DEFAULT_SELECT_TEMPLATE,
			new MustacheResultSetTemplate(
			"{\t\"metaData\" :{{>MetaData}},\n\t\"records\":{{>AllRecordsAsObjects}}\n}",
			QueryProcessorServlet.JSON_CONTENT_TYPE));
/**		
		templates.put(
			"DatabaseMetaDataSpecific",
			new MustacheResultSetTemplate(
			"{\t\"allProceduresAreCallable\" : {{allProceduresAreCallable}} }",
			QueryProcessorServlet.JSON_CONTENT_TYPE));
**/
		com.samskivert.mustache.Mustache.Compiler compiler = Mustache.compiler().nullValue("NULL").escapeHTML(false).withLoader(
			new Mustache.TemplateLoader() {
				@Override
				public Reader getTemplate(String name) throws Exception {
					return new StringReader(templates.get(name).getCode());
				}
			});

		// Compile templates
		for (Map.Entry<String, MustacheResultSetTemplate> entry : templates.entrySet()) {
			entry.getValue().setCompiledTemplate(compiler.compile(entry.getValue().getCode()));
		}
	}

	@Override
	public void processTemplate(String templateName, IterableResultSet rs, PrintWriter out) throws SQLException {
		Template t = this.chooseTemplate(templateName, rs).getCompiledTemplate();
		t.execute(rs, out);
	}

	@Override
	public String getContentType(String templateName, IterableResultSet rs) {
		MustacheResultSetTemplate t = this.chooseTemplate(templateName, rs);
		return t.getContentType();
	}

	protected MustacheResultSetTemplate chooseTemplate(String templateName, IterableResultSet rs) {
		if (templateName == null) {
			templateName = DEFAULT_SELECT_TEMPLATE; // by default
			if (rs.getUpdateCount() != -1) { // it is an INSERT or a UPDATE, choose an appropriate template				
				templateName = rs.getRecords().hasNext() ? DEFAULT_INSERT_TEMPLATE : DEFAULT_UPDATE_TEMPLATE;
			}
		}
		return templates.get(templateName);
	}

	@Override
	public Set<String> getTemplateNames() {
		return templates.keySet();
	}
}
