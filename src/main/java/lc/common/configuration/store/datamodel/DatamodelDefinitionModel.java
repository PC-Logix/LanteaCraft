package lc.common.configuration.store.datamodel;

import java.util.HashMap;

import lc.common.configuration.model.IConfigObject;
import lc.common.configuration.model.IConfigObjectList;
import lc.common.configuration.model.ModelConfigList;
import lc.common.configuration.model.ModelConfigNode;
import lc.common.configuration.model.ModelDataNode;
import lc.common.configuration.store.ConfigObjectXMLParser;
import lc.common.configuration.store.ConfigObjectXMLWriter;
import lc.common.configuration.xml.DOMHelper;
import lc.common.configuration.xml.XMLParserException;
import lc.common.configuration.xml.XMLSaverException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DatamodelDefinitionModel implements IXMLDataModel {

	public DatamodelDefinitionModel() {
		// TODO No constructor required for now.
	}

	@Override
	public IConfigObject createRootNode(ConfigObjectXMLParser parser, Node root) {
		ModelConfigNode anode = new ModelConfigNode("ConfDef");
		return anode;
	}

	@Override
	public IConfigObjectList createRootList(ConfigObjectXMLParser parser, Node root) {
		ModelConfigList alist = new ModelConfigList("ConfDef");
		return alist;
	}

	@Override
	public IConfigObject readNode(ConfigObjectXMLParser parser, IConfigObjectList parent, Element node)
			throws XMLParserException {
		IConfigObject anode = null;
		if (DOMHelper.isNodeOfType(node, "ConfRoot", false)) {
			if (!node.hasChildNodes())
				throw new XMLParserException("ConfRoot nodes must define child nodes.");
			anode = new ModelConfigList("ConfRoot");
		} else if (DOMHelper.isNodeOfType(node, "ConfTree", false)) {
			if (!node.hasChildNodes())
				throw new XMLParserException("ConfTree nodes must define child nodes.");
			anode = new ModelConfigList("ConfTree");
		} else if (DOMHelper.isNodeOfType(node, "ConfNode", false)) {
			if (!node.hasChildNodes())
				anode = new ModelConfigNode("ConfNode");
			else
				anode = new ModelConfigList("ConfNode");
		} else if (DOMHelper.isNodeOfType(node, "Parameter", false)) {
			if (node.hasChildNodes())
				throw new XMLParserException("Parameter nodes must not define child nodes.");
			if (!node.hasAttributes())
				throw new XMLParserException("Parameter nodes must define attributes.");
			anode = new ModelConfigNode("Parameter");
		} else if (DOMHelper.isNodeOfType(node, "Comment", false)) {
			if (!node.hasChildNodes())
				throw new XMLParserException("Comment nodes must define child nodes.");
			ModelDataNode comment = new ModelDataNode("Comment");
			comment.setData(DOMHelper.exportCDATA(node));
			anode = comment;
		}
		if (anode == null)
			throw new XMLParserException(String.format("Unexpected node %s", node.getLocalName()));
		return anode;
	}

	@Override
	public void applyRootAttributes(ConfigObjectXMLParser parser, IConfigObject root, Node data) {
		// TODO No root attributes for now.
	}

	@Override
	public void applyAttributes(ConfigObjectXMLParser parser, IConfigObject node, Element data)
			throws XMLParserException {
		if (node instanceof ModelConfigNode) {
			ModelConfigNode anode = (ModelConfigNode) node;
			String aname = anode.name();
			if (aname.equals("ConfRoot") || aname.equals("ConfTree") || aname.equals("ConfNode")) {
				HashMap<String, Object> parameters = new HashMap<String, Object>();
				NamedNodeMap nodes = data.getAttributes();
				for (int i = 0; i < nodes.getLength(); i++)
					parameters.put(nodes.item(i).getNodeName(), nodes.item(i).getNodeValue());
				if (parameters.get("Name") == null)
					throw new XMLParserException(aname + " node attributes must define name.");
				anode.setParameters(parameters);
			} else if (aname.equals("Parameter")) {
				HashMap<String, Object> parameters = new HashMap<String, Object>();
				NamedNodeMap nodes = data.getAttributes();
				for (int i = 0; i < nodes.getLength(); i++)
					parameters.put(nodes.item(i).getNodeName(), nodes.item(i).getNodeValue());
				if (parameters.get("Name") == null)
					throw new XMLParserException("Parameter node attributes must define name.");
				if (parameters.get("Type") == null)
					throw new XMLParserException("Parameter node attributes must define type.");
				anode.setParameters(parameters);
			}
		}
	}

	@Override
	public void writeConfigList(ConfigObjectXMLWriter writer, Element node, IConfigObjectList list)
			throws XMLSaverException {
		throw new XMLSaverException("This datamodel does not support serialization.");
	}

	@Override
	public void writeConfigObject(ConfigObjectXMLWriter writer, Element node, IConfigObject obj)
			throws XMLSaverException {
		throw new XMLSaverException("This datamodel does not support serialization.");
	}

}
