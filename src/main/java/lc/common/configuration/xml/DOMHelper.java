package lc.common.configuration.xml;

import java.util.ArrayList;

import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML DOM checking helper. Dutifully borrowed from one of my other projects.
 *
 * @author AfterLifeLochie
 */
public class DOMHelper {

	/**
	 * Attempts to match the provided node to the Element type with the tag name
	 * provided.
	 *
	 * @param node
	 *            The node object.
	 * @param tagName
	 *            The tag name.
	 * @param caseSensitive
	 *            If the match should be treated as case-sensitive.
	 * @return If the Node object is an Element and has a tag name of that
	 *         provided.
	 */
	public static boolean isNodeOfType(Node node, String tagName, boolean caseSensitive) {
		if (!(node instanceof Element))
			return false;
		Element nodeAsElement = (Element) node;
		if (caseSensitive && nodeAsElement.getTagName().equals(tagName))
			return true;
		if (!caseSensitive && nodeAsElement.getTagName().equalsIgnoreCase(tagName))
			return true;
		return false;
	}

	/**
	 * Attempts to pop the first node of type Element from the list with the
	 * provided tag name using the
	 * {@link DOMHelper#isNodeOfType(Node, String, boolean)} checker.
	 *
	 * @param list
	 *            The NodeList object.
	 * @param tagName
	 *            The tag name.
	 * @param caseSensitive
	 *            If the match should be treated as case-sensitive.
	 * @return The first Node object which is an Element and has a tag name of
	 *         that provided, or null if no such item exists.
	 */
	public static Node findNode(NodeList list, String tagName, boolean caseSensitive) {
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (isNodeOfType(node, tagName, caseSensitive))
				return node;
		}
		return null;
	}

	/**
	 * Attempts to list all of the nodes of type Element from the list with the
	 * provided tag name using the
	 * {@link DOMHelper#isNodeOfType(Node, String, boolean)} checker.
	 *
	 * @param list
	 *            The NodeList object.
	 * @param tagName
	 *            The tag name.
	 * @param caseSensitive
	 *            If the match should be treated as case-sensitive.
	 * @return A list of all Node objects which are an Element and have a tag
	 *         name of that provided, or an empty array if no such item exists.
	 */
	public static Node[] findNodesOf(NodeList list, String tagName, boolean caseSensitive) {
		ArrayList<Node> result = new ArrayList<Node>();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (isNodeOfType(node, tagName, caseSensitive))
				result.add(node);
		}
		return result.toArray(new Node[0]);
	}

	/**
	 * Attempts to find a leading comment above this node until either a Comment
	 * is encountered or another Element is found instead.
	 *
	 * @param node
	 *            The node to search for a leading comment to.
	 * @return A leading comment or null.
	 */
	public static Comment findLeadingComment(Node node) {
		Node leadingNode = node.getPreviousSibling();
		while (leadingNode != null && !(leadingNode instanceof Element) && !(leadingNode instanceof Comment))
			leadingNode = leadingNode.getPreviousSibling();
		if (leadingNode != null && leadingNode instanceof Comment)
			return (Comment) leadingNode;
		return null;
	}

	/**
	 * Checks if the Element contains all of the attributes contained in the
	 * list of labels.
	 *
	 * @param element
	 *            The Element object.
	 * @param labels
	 *            The list of string labels to test.
	 * @return If the Element contains all of the labels specified.
	 */
	public static boolean hasAllAttributes(Element element, String[] labels) {
		for (String label : labels)
			if (!element.hasAttribute(label))
				return false;
		return true;
	}

	/**
	 * Checks if the Element contains any of the attributes contained in the
	 * list of labels.
	 *
	 * @param element
	 *            The Element object.
	 * @param labels
	 *            The list of string labels to test.
	 * @return If the Element contains any of the labels specified.
	 */
	public static boolean hasAnyAttributes(Element element, String[] labels) {
		for (String label : labels)
			if (element.hasAttribute(label))
				return true;
		return false;
	}

	/**
	 * Checks if the Element contains all of the attributes contained in the
	 * list of labels, or throws an {@link XMLParserException} if it does not.
	 *
	 * @param element
	 *            The Element object.
	 * @param labels
	 *            The list of string labels to test.
	 * @throws XMLParserException
	 *             If the Element object does not contain an element required in
	 *             the list of labels, an {@link XMLParserException} will be
	 *             thrown.
	 */
	public static void checkedAllAttributes(Element element, String[] labels) throws XMLParserException {
		for (String label : labels)
			if (!element.hasAttribute(label))
				throw new XMLParserException(
						String.format("Tag %s missing attribute %s.", element.getNodeName(), label));
		return;
	}

	/**
	 * Checks if the Element contains any of the attributes contained in the
	 * list of labels, or throws an {@link XMLParserException} if it does not.
	 *
	 * @param element
	 *            The Element object.
	 * @param labels
	 *            The list of string labels to test.
	 * @throws XMLParserException
	 *             If the Element object does not contain at least one of the
	 *             elements tested in the list of labels, an
	 *             {@link XMLParserException} will be thrown.
	 */
	public static void checkedAnyAttributes(Element element, String[] labels) throws XMLParserException {
		for (String label : labels)
			if (element.hasAttribute(label))
				return;
		throw new XMLParserException(String.format("Tag %s missing at least one of any attribute: %s.",
				element.getNodeName(), listToString(labels)));
	}

	/**
	 * Converts the list of labels to a string.
	 *
	 * @param labels
	 *            The list of labels.
	 * @return The string version of the list.
	 */
	private static String listToString(String[] labels) {
		StringBuilder s = new StringBuilder();
		for (String j : labels)
			s.append(j).append(", ");
		String s1 = s.toString();
		return s1.substring(0, s1.length() - 2);
	}

	/**
	 * Attempts to pop a boolean from a string, matching a literal set of legal
	 * values that equal logical true. All other inputs are considered false.
	 *
	 * @param input
	 *            The input string.
	 * @param caseSensitive
	 *            If the checking should be case-sensitive.
	 * @return If the string matches a literal logical true.
	 */
	public static boolean popBoolean(String input, boolean caseSensitive) {
		String[] legals = { "1", "true", "on", "yes", "enabled" };
		for (String legal : legals)
			if (caseSensitive && input.equals(legal))
				return true;
			else if (!caseSensitive && input.equalsIgnoreCase(legal))
				return true;
		return false;
	}

	/**
	 * Attempts to export the first found CDATA section within the element
	 * provided.
	 * 
	 * @param node
	 *            The node to probe.
	 * @return The exported first found CDATA section's text.
	 * @throws XMLParserException
	 *             If no CDATA is found an XMLParserException will be thrown.
	 */
	public static String exportCDATA(Element node) throws XMLParserException {
		NodeList alist = node.getChildNodes();
		for (int i = 0; i < alist.getLength(); i++)
			if (alist.item(i).getNodeType() == Element.CDATA_SECTION_NODE)
				return alist.item(i).getTextContent();
		throw new XMLParserException("Couldn't find CDATA section in block.");
	}
}