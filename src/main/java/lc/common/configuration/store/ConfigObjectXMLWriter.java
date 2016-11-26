package lc.common.configuration.store;

import java.io.FileOutputStream;

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

import lc.common.configuration.model.IConfigObject;
import lc.common.configuration.model.IConfigObjectList;
import lc.common.configuration.store.datamodel.IXMLDataModel;
import lc.common.configuration.xml.XMLSaverException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConfigObjectXMLWriter {

	private final DocumentBuilderFactory factory;
	private final IXMLDataModel model;

	/** Default constructor */
	public ConfigObjectXMLWriter(IXMLDataModel model) {
		this.model = model;
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(false);
	}

	/**
	 * Save the XML document to a file.
	 *
	 * @param list
	 *            The root element
	 * @param output
	 *            The output file
	 * @throws XMLSaverException
	 *             Any XML writing failure
	 */
	public void write(IConfigObject root, FileOutputStream output) throws XMLSaverException {
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			if (root instanceof IConfigObjectList) {
				model.writeConfigList(this, (Element) doc, (IConfigObjectList) root);
			} else {
				model.writeConfigObject(this, (Element) doc, root);
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(output);
			transformer.transform(source, result);
			try {
				output.flush();
				output.close();
			} catch (Throwable t) {
				throw new XMLSaverException("Failed to save file to disk.", t);
			}
		} catch (ParserConfigurationException e) {
			throw new XMLSaverException("Can't save; configuration exception.", e);
		} catch (TransformerConfigurationException e) {
			throw new XMLSaverException("Can't save; TransformerConfigurationException occured.", e);
		} catch (TransformerException e) {
			throw new XMLSaverException("Can't save; TransformerException occured.", e);
		}
	}
}
