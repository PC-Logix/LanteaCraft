package lc.common.configuration;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

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

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLSaver {

	private final DocumentBuilderFactory factory;

	public XMLSaver() {
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(false);
	}

	public void save(ModuleList list, FileOutputStream output) throws XMLSaverException {
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			saveModuleList(doc, list);

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
			}
		} catch (ParserConfigurationException e) {
			throw new XMLSaverException("Can't save; configuration exception.", e);
		} catch (TransformerConfigurationException e) {
			throw new XMLSaverException("Can't save; TransformerConfigurationException occured.", e);
		} catch (TransformerException e) {
			throw new XMLSaverException("Can't save; TransformerException occured.", e);
		}
	}

	private void saveObject(Document document, Element element, ConfigNode node) throws XMLSaverException {
		if (node instanceof ConfigList)
			saveConfigList(document, element, (ConfigList) node);
		else
			saveConfigNode(document, element, node);
	}

	private void saveParams(Element element, HashMap<String, Object> paramlist) {
		if (paramlist == null || paramlist.size() == 0)
			return;
		for (Entry<String, Object> param : paramlist.entrySet())
			element.setAttribute(param.getKey(), param.getValue().toString());
	}

	private void saveModuleList(Document document, ModuleList modules) throws XMLSaverException {
		Element root = document.createElement("ModConfig");
		for (ModuleConfig module : modules.children()) {
			Element moduleElement = document.createElement("Module");
			saveModuleConfig(document, moduleElement, module);
			root.appendChild(moduleElement);
			if (module.comment() != null) {
				Comment moduleComment = document.createComment(module.comment());
				root.insertBefore(moduleComment, moduleElement);
			}
		}
		document.appendChild(root);
	}

	private void saveModuleConfig(Document document, Element moduleElement, ModuleConfig container)
			throws XMLSaverException {
		saveParams(moduleElement, container.parameters());
		for (ConfigNode node : container.children()) {
			Element childElement = document.createElement(node.name());
			saveObject(document, childElement, node);
			moduleElement.appendChild(childElement);
			if (node.comment() != null) {
				Comment childComment = document.createComment(node.comment());
				moduleElement.insertBefore(childComment, childElement);
			}
		}
	}

	private void saveConfigNode(Document document, Element element, ConfigNode nodeOf) throws XMLSaverException {
		saveParams(element, nodeOf.parameters());
	}

	private void saveConfigList(Document document, Element element, ConfigList listOf) throws XMLSaverException {
		saveParams(element, listOf.parameters());
		for (ConfigNode node : listOf.children()) {
			Element childElement = document.createElement(node.name());
			saveObject(document, childElement, node);
			element.appendChild(childElement);
			if (node.comment() != null) {
				Comment childComment = document.createComment(node.comment());
				element.insertBefore(childComment, childElement);
			}
		}
	}
}
