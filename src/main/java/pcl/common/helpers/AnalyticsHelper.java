package pcl.common.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;

import net.minecraftforge.common.ForgeVersion;
import pcl.lc.BuildInfo;
import pcl.lc.LanteaCraft;
import cpw.mods.fml.relauncher.FMLLaunchHandler;

/**
 * Analyics submission thread. Prevents the entire proxy exploding.
 * 
 * @author AfterLifeLochie
 */
public class AnalyticsHelper extends Thread {
	private final String charset = "UTF-8";
	private final String server = "http://www.pc-logix.com/SGCraft_analytics/";
	private final StringBuilder report;
	private final int maxTries = 3;

	private int tries = 0;
	private boolean overrideValidity = false;

	public AnalyticsHelper(boolean submitAnyway, String[] reportData) {
		super();
		setDaemon(true);
		setName("LanteaCraft analytics submission thread");
		report = new StringBuilder();
		overrideValidity = submitAnyway;

		try {
			if (reportData != null) {
				report.append(pack("version", reportData[0]));
				report.append(pack("side", reportData[1]));
				report.append(pack("forge", reportData[2]));
			} else {
				StringBuilder buildName = new StringBuilder();
				buildName.append(BuildInfo.modName).append(" ");
				buildName.append(BuildInfo.versionNumber).append(" ");
				buildName.append("build ").append(BuildInfo.buildNumber);
				report.append(pack("version", buildName.toString()));
				report.append(pack("side", FMLLaunchHandler.side().name()));
				report.append(pack("forge", ForgeVersion.getVersion()));
			}
		} catch (UnsupportedEncodingException e) {
			LanteaCraft.getLogger().log(Level.WARNING, "This client is weird and doesn't suppport UTF-8, giving up.");
			tries += maxTries;
		}
	}

	@Override
	public void run() {
		if (BuildInfo.buildNumber.equals("@" + "BUILD" + "@") && !overrideValidity)
			return;

		while (tries < maxTries)
			try {
				tries++;
				LanteaCraft.getLogger().log(Level.FINE, "Pushing metrics data (try " + tries + " of " + maxTries + ")");
				push();
				LanteaCraft.getLogger().log(Level.FINE, "Done submitting anonymous data.");
				return;
			} catch (IOException ioex) {
				LanteaCraft.getLogger().log(Level.WARNING, "The metrics push failed.", ioex);
			}
		LanteaCraft.getLogger().log(Level.WARNING, "Failed to push metrics data, maximum tries exceeded!");
	}
	
	public String getReportData() {
		return report.toString();
	}

	private String pack(String arg, String val) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		result.append(arg).append("=");
		result.append(URLEncoder.encode(val, charset)).append("&");
		return result.toString();
	}

	private void push() throws IOException {
		HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(server).openConnection();
		httpUrlConnection.setDoOutput(true);
		httpUrlConnection.setConnectTimeout(30 * 1000);
		httpUrlConnection.setRequestProperty("Accept-Charset", charset);
		httpUrlConnection.setRequestMethod("POST");

		OutputStream os = httpUrlConnection.getOutputStream();
		os.write(report.toString().getBytes(charset));
		os.flush();
		os.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
		String s = null;
		StringBuilder resp = new StringBuilder();
		while ((s = in.readLine()) != null) {
			resp.append(s);
			if (s.trim().equals("OK")) {
				in.close();
				return;
			}
		}
		in.close();
		throw new IOException("Unexpected metrics server response: " + resp.toString());
	}
}
