package pcl.common.xmlcfg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {

	private final DocumentBuilderFactory factory;

	public XMLParser() {
		this.factory = DocumentBuilderFactory.newInstance();
	}

	/**
	 * Attempts to read a configuration file structure from the file
	 * 
	 * @param chunk
	 * @return
	 * @throws XMLParserException
	 */
	public ConfigList read(InputStream chunk) throws XMLParserException {
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(chunk);
			NodeList root = doc.getDocumentElement().getChildNodes();
			Node modRoot = DOMHelper.findNode(root, "ModConfig", false);
			if (modRoot == null)
				throw new XMLParserException("Missing ModConfig root tag.");
			return readRoot(modRoot);
		} catch (IOException e) {
			throw new XMLParserException("Can't parse; IOException occured.", e);
		} catch (ParserConfigurationException e) {
			throw new XMLParserException("Can't parse; configuration exception.", e);
		} catch (SAXException e) {
			throw new XMLParserException("Can't parse; document syntax exception.", e);
		}
	}

	private ConfigList readRoot(Node modRoot) throws XMLParserException {
		ConfigList root = new ConfigList("ModConfig");
		ArrayList<ModuleConfig> rootChildren = new ArrayList<ModuleConfig>();
		NodeList childrenRoot = modRoot.getChildNodes();
		for (int i = 0; i < childrenRoot.getLength(); i++) {
			Node child = childrenRoot.item(i);
			if (DOMHelper.isNodeOfType(child, "Module", false)) {
				rootChildren.add(readModuleConfig((Element) child));
			}
		}
		return root;
	}

	private ModuleConfig readModuleConfig(Element moduleNode) throws XMLParserException {
		DOMHelper.checkedAllAttributes(moduleNode, new String[] { "name", "enabled" });
		ModuleConfig moduleRoot = new ModuleConfig(moduleNode.getAttribute("name"));
		ArrayList<ConfigNode> rootChildren = new ArrayList<ConfigNode>();
		NodeList childrenRoot = moduleNode.getChildNodes();
		for (int i = 0; i < childrenRoot.getLength(); i++) {
			Node child = childrenRoot.item(i);
			if (child instanceof Element)
				rootChildren.add(readRecusriveObject((Element) child));
		}
		return moduleRoot;
	}

	private ConfigNode readRecusriveObject(Element element) throws XMLParserException {
		if (element.hasChildNodes()) {
			ConfigList group = new ConfigList(element.getTagName());
			ArrayList<ConfigNode> childrenGroup = new ArrayList<ConfigNode>();
			if (element.hasAttributes()) {
				HashMap<String, String> parameters = new HashMap<String, String>();
				NamedNodeMap nodes = element.getAttributes();
				// TOOD: handling of attributes on xml-tag
			}
			NodeList children = element.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element)
					childrenGroup.add(readRecusriveObject((Element) child));
			}
			return group;
		} else {
			ConfigNode single = new ConfigNode(element.getTagName());
			if (element.hasAttributes()) {
				HashMap<String, String> parameters = new HashMap<String, String>();
				NamedNodeMap nodes = element.getAttributes();
				// TOOD: handling of attributes on xml-tag
			}
			return single;
		}
	}
}
