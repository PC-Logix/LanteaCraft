package lc.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map.Entry;

import lc.common.LCLog;
import lc.core.BuildInfo;
import net.minecraftforge.common.MinecraftForge;

/**
 * Version checking thread.
 *
 * @author AfterLifeLochie
 *
 */
public class VersionHelper extends Thread {

	private final String charset = "UTF-8";
	private final int maxTries = 3;
	private final String serverPath;
	private String report;
	private int tries = 0;

	/** Remote version data */
	public String remoteVersion;
	/** Remote version data */
	public String remoteLabel;
	/** Remote version data */
	public int remoteBuild;
	/** Has finished ? */
	public boolean finished = false;
	/** Needs notification ? */
	public boolean requiresNotify = false;

	/** Default constructor */
	public VersionHelper(HashMap<String, String> query) {
		super();
		StringBuilder path = new StringBuilder();
		path.append(BuildInfo.webAPI).append("build/");
		path.append(MinecraftForge.MC_VERSION.replace(".", "_")).append("/");
		path.append("latest");
		serverPath = path.toString();

		try {
			StringBuilder reportBuilder = new StringBuilder();
			for (Entry<String, String> param : query.entrySet())
				reportBuilder.append(packParam(param.getKey(), param.getValue()));
			report = reportBuilder.toString();
		} catch (UnsupportedEncodingException e) {
			LCLog.warn("This client is weird and doesn't suppport UTF-8, giving up.");
			tries += maxTries;
		}

		setDaemon(true);
		setName("LanteaCraft version poll thread");
	}

	private String packParam(String arg, String val) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		result.append(arg).append("=");
		result.append(URLEncoder.encode(val, charset)).append("&");
		return result.toString();
	}

	@Override
	public void run() {
		while (!finished && tries < maxTries)
			try {
				tries++;
				LCLog.debug("Querying version serverPath (try " + tries + " of " + maxTries + ")");
				runTask();
			} catch (IOException ioex) {
				LCLog.warn("The version metadata pull failed.", ioex);
			}
		if (maxTries >= tries)
			LCLog.warn("Failed to pull version data, maximum tries exceeded!");
	}

	private void runTask() throws IOException {
		HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(serverPath).openConnection();
		httpUrlConnection.setConnectTimeout(30 * 1000);
		httpUrlConnection.setRequestProperty("Accept-Charset", charset);

		if (report != null) {
			httpUrlConnection.setRequestMethod("POST");
			OutputStream os = httpUrlConnection.getOutputStream();
			os.write(report.getBytes(charset));
			os.flush();
			os.close();
		} else
			httpUrlConnection.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
		StringBuilder feed = new StringBuilder();
		String buffer, result;
		while ((buffer = in.readLine()) != null)
			feed.append(buffer);
		in.close();
		result = feed.toString();

		try {
			String[] section = result.split(":");
			String[] versionAndBuild = section[0].split("-");
			StringBuilder version = new StringBuilder();
			for (int i = 0; i < versionAndBuild.length; i++)
				if (i == versionAndBuild.length - 1)
					remoteBuild = Integer.parseInt(versionAndBuild[i]);
				else
					version.append(versionAndBuild[i]).append("-");
			String versionStr = version.toString();
			remoteVersion = versionStr.substring(0, versionStr.length() - 1);
			remoteLabel = section[1].trim();

			if (remoteBuild > BuildInfo.getBuildNumber())
				requiresNotify = true;
			finished = true;
		} catch (Throwable t) {
			throw new IOException("Strange version sever response: `" + result + "`");
		}
	}
}
