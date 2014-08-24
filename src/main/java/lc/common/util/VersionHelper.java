package lc.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import lc.common.LCLog;
import lc.core.BuildInfo;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;

public class VersionHelper extends Thread {

	private final String charset = "UTF-8";
	private final int maxTries = 3;
	private final String server;
	private int tries = 0;

	public String remoteVersion;
	public String remoteLabel;
	public int remoteBuild;
	public boolean finished = false;
	public boolean requiresNotify = false;

	public VersionHelper() {
		super();
		StringBuilder serverPath = new StringBuilder();
		serverPath.append(BuildInfo.webAPI).append("build/");
		serverPath.append(MinecraftForge.MC_VERSION.replace(".", "_")).append("/");
		serverPath.append("latest");
		server = serverPath.toString();
		setDaemon(true);
		setName("LanteaCraft version poll thread");
	}

	@Override
	public void run() {
		while (tries < maxTries)
			try {
				tries++;
				LCLog.debug("Querying version server (try " + tries + " of " + maxTries + ")");
				pullAndParse();
				finished = true;
				return;
			} catch (IOException ioex) {
				LCLog.warn("The version metadata pull failed.", ioex);
			}
		LCLog.warn("Failed to pull version data, maximum tries exceeded!");
	}

	private void pullAndParse() throws IOException {
		HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(server).openConnection();
		httpUrlConnection.setConnectTimeout(30 * 1000);
		httpUrlConnection.setRequestProperty("Accept-Charset", charset);
		httpUrlConnection.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
		StringBuilder feed = new StringBuilder();
		String buffer;
		while ((buffer = in.readLine()) != null)
			feed.append(buffer);
		in.close();
		String result = feed.toString();

		try {
			String[] section = result.split(":");
			String[] versionAndBuild = section[0].split("-");
			StringBuilder version = new StringBuilder();
			for (int i = 0; i < versionAndBuild.length; i++)
				if (i == (versionAndBuild.length - 1))
					remoteBuild = Integer.parseInt(versionAndBuild[i]);
				else
					version.append(versionAndBuild[i]).append("-");
			String versionStr = version.toString();
			remoteVersion = versionStr.substring(0, versionStr.length() - 1);
			remoteLabel = section[1].trim();

			if (remoteBuild > BuildInfo.getBuildNumber())
				requiresNotify = true;
		} catch (Throwable t) {
			throw new IOException("Strange version sever response: `" + result + "`");
		}
	}
}
