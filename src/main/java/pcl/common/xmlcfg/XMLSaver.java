package pcl.common.xmlcfg;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class XMLSaver {

	private final DocumentBuilderFactory factory;

	public XMLSaver() {
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);

	}

	public void save(ConfigList list) throws XMLSaverException {
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			saveObject(doc, list);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StringWriter out = new StringWriter();
			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			throw new XMLSaverException("Can't parse; configuration exception.", e);
		} catch (TransformerConfigurationException e) {
			throw new XMLSaverException("Can't parse; TransformerConfigurationException occured.", e);
		} catch (TransformerException e) {
			throw new XMLSaverException("Can't parse; TransformerException occured.", e);
		}
	}

	private void saveObject(Document document, ConfigNode node) throws XMLSaverException {
		if (node instanceof ModuleConfig) {

		} else if (node instanceof ConfigList)
			saveConfigList(document, (ConfigList) node);
		else
			saveConfigNode(document, node);
	}

	private void saveModuleConfig(Document document, ModuleConfig container) throws XMLSaverException {

	}

	private void saveConfigNode(Document document, ConfigNode nodeOf) throws XMLSaverException {

	}

	private void saveConfigList(Document document, ConfigList listOf) throws XMLSaverException {

	}
}
