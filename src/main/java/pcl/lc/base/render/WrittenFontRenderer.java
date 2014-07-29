package pcl.lc.base.render;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pcl.lc.LanteaCraft;
import pcl.lc.io.StackedPushbackStringReader;

/**
 * Renders a written-like typeface in game using natural writing styles.
 * 
 * @author AfterLifeLochie
 */
public class WrittenFontRenderer {

	/**
	 * Metrics about a character
	 * 
	 * @author AfterLifeLochie
	 */
	private class GlyphMetric {
		public int width, height;
		public int ux, vy;

		public GlyphMetric(int w, int h, int u, int v) {
			width = w;
			height = h;
			ux = u;
			vy = v;
		}
	}

	/**
	 * One formatted line with a spacing and line-height
	 * 
	 * @author AfterLifeLochie
	 */
	public static class BoxedLine {
		public final String line;
		public final int space_size;
		public final int line_height;

		public BoxedLine(String line, int space_size, int line_height) {
			this.line = line;
			this.space_size = space_size;
			this.line_height = line_height;
		}
	}

	/**
	 * One whole page containing a collection of spaced lines with line-heights
	 * and inside a page margin (gutters).
	 * 
	 * @author AfterLifeLochie
	 */
	public static class PageBox {
		public final int page_width, page_height;
		public final int margin_left, margin_right;
		public final int min_space_size;
		public LinkedList<BoxedLine> lines = new LinkedList<BoxedLine>();

		public PageBox(int w, int h, int ml, int mr, int min_sp) {
			page_width = w;
			page_height = h;
			margin_left = ml;
			margin_right = mr;
			min_space_size = min_sp;
		}

		public int getFreeHeight() {
			int h = page_height;
			for (BoxedLine line : lines)
				h -= line.line_height;
			return h;
		}

	}

	/**
	 * The font image
	 */
	private final ResourceLocation fontImageName;
	/**
	 * The font name
	 */
	private final ResourceLocation fontMetricName;
	/**
	 * The individual dimensions and u-v locations of each character in the set
	 */
	private final HashMap<Integer, GlyphMetric> char_metrics = new HashMap<Integer, GlyphMetric>();

	public WrittenFontRenderer(ResourceLocation fontImageName, ResourceLocation fontMetricName) {
		this.fontImageName = fontImageName;
		this.fontMetricName = fontMetricName;
	}

	/**
	 * Attempts to build the font
	 */
	public void buildFont() {
		try {
			IResource metricResource = Minecraft.getMinecraft().getResourceManager().getResource(fontMetricName);
			InputStream stream = metricResource.getInputStream();
			if (stream == null)
				throw new IOException("Could not open font metric file.");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(stream);
			Element metrics = doc.getDocumentElement();

			NodeList list_character = metrics.getElementsByTagName("character");
			for (int i = 0; i < list_character.getLength(); i++) {
				Element character = (Element) list_character.item(i);
				int charcode = Integer.parseInt(character.getAttributes().getNamedItem("key").getNodeValue());
				if (0 > charcode || charcode > 255)
					throw new IOException(String.format("Unsupported character code %s", charcode));
				int w = -1, h = -1, u = -1, v = -1;
				NodeList character_properties = character.getChildNodes();
				for (int k = 0; k < character_properties.getLength(); k++) {
					Node property = character_properties.item(k);
					if (!(property instanceof Element))
						continue;
					Element elem = (Element) property;
					String name = elem.getNodeName().toLowerCase();
					int val = Integer.parseInt(elem.getFirstChild().getNodeValue());
					if (name.equals("width"))
						w = val;
					else if (name.equals("height"))
						h = val;
					else if (name.equals("x"))
						u = val;
					else if (name.equals("y"))
						v = val;
					else
						throw new IOException(String.format("Unexpected metric command %s", name));
				}
				if (w == -1 || h == -1 || u == -1 || v == -1)
					throw new IOException(String.format("Invalid metric properties set for key %s", charcode));
				char_metrics.put(charcode, new GlyphMetric(w, h, u, v));
			}
		} catch (IOException e) {
			LanteaCraft.getLogger().log(Level.WARN, "Cannot setup font.", e);
		} catch (ParserConfigurationException e) {
			LanteaCraft.getLogger().log(Level.WARN, "Cannot read font metric data.", e);
		} catch (SAXException e) {
			LanteaCraft.getLogger().log(Level.WARN, "Cannot read font metric data.", e);
		}
		LanteaCraft.getLogger()
				.log(Level.INFO,
						String.format("Read %s metric definitions from file %s", char_metrics.size(),
								fontMetricName.toString()));
	}

	/**
	 * Attempt to box a line or part of a line onto a PageBox. This immediately
	 * attempts to fit as much of the line onto a LineBox and then glues it to
	 * the tail of a PageBox if the PageBox can support the addition of a line.
	 * Any overflow text which cannot be boxed onto the page is returned.
	 * 
	 * @param line
	 *            The line
	 * @param page
	 *            The page to box onto
	 * @return If a page overflow occurs - that is, if there is no more
	 *         available vertical space for lines to occupy.
	 */
	private boolean boxLine(StackedPushbackStringReader text, PageBox page) throws IOException {
		// Calculate some required properties
		int effectiveWidth = page.page_width - page.margin_left - page.margin_right;
		int effectiveHeight = page.getFreeHeight();

		int width_new_line = 0, width_new_word = 0;
		// Start globbing characters
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<Character> chars = new ArrayList<Character>();
		// Push our place in case we have to abort
		text.pushPosition();
		while (text.available() > 0) {
			// Take a char
			char c = text.next();
			// Treat space as a word separator
			if (c == ' ') {
				// Push a whole word if one exists
				if (chars.size() > 0) {
					// Find out if there is enough space to push this word
					int new_width_nl = width_new_line + width_new_word + page.min_space_size;
					if (effectiveWidth >= new_width_nl) {
						// Yes, there is enough space, add the word
						width_new_line += width_new_word;
						words.add(String.valueOf(chars.toArray(new Character[0])));
						// Clear the character buffers
						chars.clear();
						width_new_word = 0;
					} else {
						// No, the word doesn't fit, back it up
						chars.clear();
						width_new_word = 0;
						break;
					}
				}
			} else {
				GlyphMetric metric = char_metrics.get((int) c);
				if (metric != null) {
					width_new_word += metric.width;
					chars.add(c);
				}
			}
		}

		// Find the maximum height of any characters in the line
		int height_new_line = 0;
		for (int i = 0; i < words.size(); i++) {
			String word = words.get(i);
			for (int j = 0; j < word.length(); j++) {
				char c = word.charAt(j);
				if (c != ' ') {
					GlyphMetric metric = char_metrics.get((int) c);
					if (metric.height > height_new_line)
						height_new_line = metric.height;
				}
			}
		}

		// If the line doesn't fit at all, we can't do anything
		if (height_new_line > effectiveHeight) {
			text.popPosition(); // back out
			return true;
		}

		// Commit our position as we have now read a line and it fits all
		// current constraints on the page
		text.commitPosition();

		// Glue the whole line together
		StringBuilder line = new StringBuilder();
		for (int i = 0; i < words.size(); i++) {
			line.append(words.get(i));
			if (i != words.size() - 1)
				line.append(" ");
		}

		// Figure out how much space is left over from the line
		int space_remain = effectiveWidth - width_new_line;
		int extra_px_per_space = (int) Math.floor(space_remain / words.size());

		// Create the linebox
		BoxedLine linebox = new BoxedLine(line.toString(), page.min_space_size + extra_px_per_space, height_new_line);
		page.lines.add(linebox);
		return false;
	}

	/**
	 * Attempt to box a paragraph or part of a paragraph onto a collection of
	 * PageBox instances.
	 * 
	 * @param text
	 *            The text blob
	 * @return The page results
	 */
	private PageBox[] boxParagraph(String text, int width, int height, int margin_l, int margin_r) throws IOException {
		StackedPushbackStringReader reader = new StackedPushbackStringReader(text);
		ArrayList<PageBox> pages = new ArrayList<PageBox>();
		PageBox currentPage = new PageBox(width, height, margin_l, margin_r, 1);
		boolean flag = false;
		while (reader.available() > 0) {
			flag = boxLine(reader, currentPage);
			if (flag) {
				pages.add(currentPage);
				currentPage = new PageBox(width, height, margin_l, margin_r, 1);
			}
		}
		if (!flag)
			pages.add(currentPage);
		return pages.toArray(new PageBox[0]);
	}
}
