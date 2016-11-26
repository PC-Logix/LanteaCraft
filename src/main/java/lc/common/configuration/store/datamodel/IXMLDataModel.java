package lc.common.configuration.store.datamodel;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import lc.common.configuration.model.IConfigObject;
import lc.common.configuration.model.IConfigObjectList;
import lc.common.configuration.store.ConfigObjectXMLParser;
import lc.common.configuration.store.ConfigObjectXMLWriter;
import lc.common.configuration.xml.XMLParserException;
import lc.common.configuration.xml.XMLSaverException;

/**
 * @author AfterLifeLochie
 *
 */
public interface IXMLDataModel {

	public IConfigObject createRootNode(ConfigObjectXMLParser parser, Node root) throws XMLParserException;

	public IConfigObjectList createRootList(ConfigObjectXMLParser parser, Node root) throws XMLParserException;

	public IConfigObject readNode(ConfigObjectXMLParser parser, IConfigObjectList parent, Element node)
			throws XMLParserException;

	public void applyRootAttributes(ConfigObjectXMLParser parser, IConfigObject root, Node data)
			throws XMLParserException;

	public void applyAttributes(ConfigObjectXMLParser parser, IConfigObject node, Element data)
			throws XMLParserException;

	public void writeConfigList(ConfigObjectXMLWriter writer, Element node, IConfigObjectList list)
			throws XMLSaverException;

	public void writeConfigObject(ConfigObjectXMLWriter writer, Element node, IConfigObject obj)
			throws XMLSaverException;

}
