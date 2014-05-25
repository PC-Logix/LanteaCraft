package pcl.lc.guis;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pcl.lc.LanteaCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Resource;
import net.minecraft.util.ResourceLocation;

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
			this.width = w;
			this.height = h;
			this.ux = u;
			this.vy = v;
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
		public LinkedList<BoxedLine> lines = new LinkedList<BoxedLine>();

		public PageBox(int w, int h, int ml, int mr) {
			this.page_width = w;
			this.page_height = h;
			this.margin_left = ml;
			this.margin_right = mr;
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
			Resource metricResource = Minecraft.getMinecraft().getResourceManager().getResource(fontMetricName);
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
					Node property = (Node) character_properties.item(k);
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
			LanteaCraft.getLogger().log(Level.WARNING, "Cannot setup font.", e);
		} catch (ParserConfigurationException e) {
			LanteaCraft.getLogger().log(Level.WARNING, "Cannot read font metric data.", e);
		} catch (SAXException e) {
			LanteaCraft.getLogger().log(Level.WARNING, "Cannot read font metric data.", e);
		}
		LanteaCraft.getLogger()
				.log(Level.INFO,
						String.format("Read %s metric definitions from file %s", char_metrics.size(),
								fontMetricName.toString()));
	}

}
