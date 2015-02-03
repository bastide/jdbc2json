package com.github.bastide.jdbc2json.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import com.github.bastide.jdbc2json.Jdbc2JsonConfig;
import org.xml.sax.*;

/**
 *
 * @author rbastide
 */
public class Jdbc2jsonParser implements ContentHandler {
        private final Jdbc2JsonConfig myConfig;
	private final Stack context;
	private final StringBuffer buffer;
	@SuppressWarnings("NonConstantLogger")
	private final Logger logger; // The logger follows the servlet's name

	public Jdbc2jsonParser(Logger logger, Jdbc2JsonConfig config) {
                myConfig = config;
		this.logger = (logger == null) ? Logger.getLogger(Jdbc2jsonParser.class.getName()) : logger;
		buffer = new StringBuffer(111);
		context = new java.util.Stack();
	}

	/**
	 *
	 * This SAX interface method is implemented by the parser.
	 */
	@Override
	public final void setDocumentLocator(Locator locator) {
	}

	/**
	 *
	 * This SAX interface method is implemented by the parser.
	 */
	@Override
	public final void startDocument() throws SAXException {
	}

	@Override
	public final void endDocument() throws SAXException {
	}

	@Override
	public final void startElement(java.lang.String ns, java.lang.String name, java.lang.String qname, org.xml.sax.Attributes attrs) throws org.xml.sax.SAXException {
		dispatch(true);
		context.push(new Object[]{qname, new org.xml.sax.helpers.AttributesImpl(attrs)});
	}

	@Override
	public final void endElement(java.lang.String ns, java.lang.String name, java.lang.String qname) throws org.xml.sax.SAXException {
		dispatch(false);
		context.pop();
	}

	@Override
	public final void characters(char[] chars, int start, int len) throws SAXException {
		buffer.append(chars, start, len);
	}

	@Override
	public final void ignorableWhitespace(char[] chars, int start, int len) throws SAXException {
	}

	@Override
	public final void processingInstruction(String target, String data) throws SAXException {
	}

	@Override
	public final void startPrefixMapping(java.lang.String prefix, java.lang.String uri) throws org.xml.sax.SAXException {
	}

	@Override
	public final void endPrefixMapping(java.lang.String prefix) throws org.xml.sax.SAXException {
	}

	@Override
	public final void skippedEntity(java.lang.String name) throws org.xml.sax.SAXException {
	}

	private void dispatch(final boolean fireOnlyIfMixed) throws SAXException {
		if (fireOnlyIfMixed && buffer.length() == 0) {
			return; //skip it
		}
		Object[] ctx = (Object[]) context.peek();
		String here = (String) ctx[0];
		org.xml.sax.Attributes attrs = (org.xml.sax.Attributes) ctx[1];
		if (null != here) {
			switch (here) {
				case "DRIVERSTRING":
					myConfig.setDriverString(buffer.toString());
					break;
				case "QUERY":
					String queryName = attrs.getValue("name");
		                        myConfig.addQuery(queryName, buffer.toString());
					break;
				case "DATASOURCE":
					myConfig.setDataSource(buffer.toString());
					break;
				case "TEMPLATE":
					String templateName = attrs.getValue("name");
					myConfig.addTemplate(templateName, buffer.toString());					
					break;
				default:
					break;
			}
		}
		buffer.delete(0, buffer.length());
	}

	public void parse(final InputStream configStream) throws ParserConfigurationException, IOException, SAXException {
			javax.xml.parsers.SAXParserFactory factory = javax.xml.parsers.SAXParserFactory.newInstance();
			factory.setValidating(true); //the code was generated according DTD
			factory.setNamespaceAware(false); //the code was generated according DTD
			XMLReader parser = factory.newSAXParser().getXMLReader();
			parser.setEntityResolver(new DTDResourceResolver());
			parser.setContentHandler(this);
			parser.setErrorHandler(new ErrorHandler() {
				@Override
				public void error(SAXParseException ex) throws SAXException {
					if (context.isEmpty()) {
						//logger.log(Level.CONFIG, "Configuration error in {0} : {1}", new String[]{fileName, ex.getMessage()});
					}
					throw ex;
				}

				@Override
				public void fatalError(SAXParseException ex) throws SAXException {
					//logger.log(Level.SEVERE, "Configuration error in {0} : {1}", new String[]{fileName, ex.getMessage()});
					throw ex;
				}

				@Override
				public void warning(SAXParseException ex) throws SAXException {
					//logger.log(Level.WARNING, "Configuration error in {0} : {1}", new String[]{fileName, ex.getMessage()});
				}
			});
                        InputSource source = new InputSource(configStream);
			parser.parse(source);		
	}
}
