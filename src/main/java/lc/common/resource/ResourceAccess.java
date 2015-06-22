package lc.common.resource;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import lc.BuildInfo;
import lc.LCRuntime;
import lc.common.LCLog;
import lc.common.configuration.xml.ComponentConfig;
import net.minecraft.util.ResourceLocation;

/**
 * Resource access controller.
 * 
 * @author AfterLifeLochie
 * 
 */
public class ResourceAccess {
	private static HashMap<String, ResourceLocation> resourceMap = new HashMap<String, ResourceLocation>();

	private static HashMap<String, ArrayList<String>> resourceAccesses = new HashMap<String, ArrayList<String>>();
	private static HashMap<String, ArrayList<String>> nameAccesses = new HashMap<String, ArrayList<String>>();

	private static final boolean logResources = BuildInfo.$.development() && true;

	private static String assetKey = "pcl_lc";

	/**
	 * Get the system asset key.
	 * 
	 * @return The system asset key.
	 */
	public static String getAssetKey() {
		return assetKey;
	}

	/**
	 * Get a resource based on a fully-qualified name.
	 * 
	 * @param resourceName
	 *            The resource name.
	 * @return A path to the resource
	 */
	public static ResourceLocation getNamedResource(String resourceName) {
		if (!resourceMap.containsKey(resourceName))
			resourceMap.put(resourceName, new ResourceLocation(getAssetKey(), resourceName));

		if (logResources) {
			StackTraceElement[] hist = Thread.currentThread().getStackTrace();
			StackTraceElement callee = hist[2];
			String top = String.format("%s:%s @ %s: %s", callee.getClassName(), callee.getMethodName(),
					callee.getFileName(), callee.getLineNumber());
			if (!resourceAccesses.containsKey(resourceName))
				resourceAccesses.put(resourceName, new ArrayList<String>());
			if (!resourceAccesses.get(resourceName).contains(top))
				resourceAccesses.get(resourceName).add(top);
		}

		return resourceMap.get(resourceName);
	}

	/**
	 * Formats a resource name using pattern matching.
	 * 
	 * @param format
	 *            The format string.
	 * @param args
	 *            Any additional formatting arguments (see
	 *            {@link String#format(String, Object...)})
	 * @return The formatted resource name.
	 */
	public static String formatResourceName(String format, Object... args) {
		ComponentConfig conf = (ComponentConfig) LCRuntime.runtime.hints().config();
		String quality = (String) conf
				.getOrSetParam("Render", "Quality", "TextureQuality", "The texture quality", "32");
		format = format.replace("${TEX_QUALITY}", quality);
		format = format.replace("${ASSET_KEY}", getAssetKey());
		String result = String.format(format, args);

		if (logResources) {
			StackTraceElement[] hist = Thread.currentThread().getStackTrace();
			StackTraceElement callee = hist[2];
			String top = String.format("%s:%s @ %s: %s", callee.getClassName(), callee.getMethodName(),
					callee.getFileName(), callee.getLineNumber());
			if (!nameAccesses.containsKey(result))
				nameAccesses.put(result, new ArrayList<String>());
			if (!nameAccesses.get(result).contains(top))
				nameAccesses.get(result).add(top);
		}

		return result;
	}

	/**
	 * Save the resource access history to file.
	 */
	public static void saveRegistry() {
		try {
			PrintStream resources = new PrintStream(new File("resourceMap.txt"));
			for (Entry<String, ArrayList<String>> access : resourceAccesses.entrySet())
				for (String source : access.getValue()) {
					resources.print(access.getKey());
					resources.print(": ");
					resources.println(source);
				}
			resources.close();

			PrintStream names = new PrintStream(new File("nameMap.txt"));
			for (Entry<String, ArrayList<String>> access : nameAccesses.entrySet())
				for (String source : access.getValue()) {
					names.print(access.getKey());
					names.print(": ");
					names.println(source);
				}
			names.close();
		} catch (IOException ioex) {
			LCLog.fatal("Failed to save log!", ioex);
		}
	}
}
