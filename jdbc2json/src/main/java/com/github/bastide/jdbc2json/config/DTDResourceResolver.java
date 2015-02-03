package com.github.bastide.jdbc2json.config;

import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.*;

public class DTDResourceResolver implements EntityResolver {

	private final static String DTD = "jdbc2json.dtd";

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		InputSource inputSource = null;
		if (null != systemId && systemId.endsWith(DTD)) {
			try {
				InputStream inputStream = DTDResourceResolver.class.getResourceAsStream(DTD);
				inputSource = new InputSource(inputStream);
			} catch (Exception e) {
			}
		}
		// If nothing found, null is returned, for normal processing
		return inputSource;
	}

}
