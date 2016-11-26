package lc.common.configuration.store;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import lc.common.configuration.model.IConfigObject;
import lc.common.configuration.model.IConfigObjectList;
import lc.common.configuration.store.datamodel.IXMLDataModel;
import lc.common.configuration.xml.DOMHelper;
import lc.common.configuration.xml.XMLParserException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConfigObjectXMLParser {

	private final DocumentBuilderFactory factory;
	private final String rootTag;
	private final IXMLDataModel model;

	/** Default constructor */
	public ConfigObjectXMLParser(IXMLDataModel model, String rootTag) {
		this.model = model;
		this.rootTag = rootTag;
		factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments(false);
	}

	/**
	 * Attempts to read at least one configuration object from the file
	 *
	 * @param chunk
	 *            The chunk to read.
	 * @return The IConfigObject object.
	 * @throws XMLParserException
	 *             Any XML or read exception.
	 */
	public IConfigObject read(InputStream chunk) throws XMLParserException {
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(chunk);
			NodeList root = doc.getChildNodes();
			Node confRoot = DOMHelper.findNode(root, rootTag, false);
			if (confRoot == null)
				throw new XMLParserException("Missing root tag");
			IConfigObject rootNode;
			if (confRoot.hasChildNodes()) {
				rootNode = (IConfigObject) model.createRootList(this, confRoot);
				populateChildNodes((IConfigObjectList) rootNode, confRoot);
			} else {
				rootNode = model.createRootNode(this, confRoot);
			}
			model.applyRootAttributes(this, rootNode, confRoot);
			return rootNode;
		} catch (IOException e) {
			throw new XMLParserException("Can't parse; IOException occured.", e);
		} catch (ParserConfigurationException e) {
			throw new XMLParserException("Can't parse; configuration exception.", e);
		} catch (SAXException e) {
			throw new XMLParserException("Can't parse; document syntax exception.", e);
		}
	}

	public void populateChildNodes(IConfigObjectList rootList, Node confRoot) throws XMLParserException {
		NodeList children = confRoot.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				IConfigObject cld = model.readNode(this, rootList, (Element) child);
				model.applyAttributes(this, cld, (Element) child);
				if (cld != null)
					rootList.addChild(cld);
			}
		}
	}

}
