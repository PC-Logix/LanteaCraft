package lc.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import lc.BuildInfo;
import lc.common.LCLog;
import lc.server.HintProviderServer;
import net.minecraftforge.common.MinecraftForge;

/**
 * LanteaCraft beacon stats & data task.
 *
 * @author AfterLifeLochie
 *
 */
public class BeaconStreamThread extends Thread {

	private final String charset = "UTF-8";
	private final int maxTries = 3;

	private final String serverPath;
	private int tries = 0;
	public boolean finished = false;

	public String report;
	public JsonElement response;

	/**
	 * Default constructor
	 *
	 * @param server
	 *            The game server
	 */
	public BeaconStreamThread(HintProviderServer server) {
		super();
		setDaemon(true);
		setName("LanteaCraft beacon worker");
		serverPath = new StringBuilder().append(BuildInfo.webAPI).append("beacon").toString();
	}

	public void beacon(HashMap<String, String> payload) {
		if (finished)
			return;
		report = new Gson().toJson(payload);
		start();
	}

	@Override
	public void run() {
		while (!finished && tries < maxTries)
			try {
				tries++;
				LCLog.debug("Performing beacon task (" + tries + "/" + maxTries + ")");
				runTask();
			} catch (IOException ioex) {
				LCLog.warn("Beacon task I/O error.", ioex);
			}
		if (!finished)
			LCLog.warn("Failed to perform beacon task.");
	}

	private void runTask() throws IOException {
		HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(serverPath).openConnection();
		httpUrlConnection.setConnectTimeout(30 * 1000);
		httpUrlConnection.setRequestProperty("Accept-Charset", charset);

		if (report != null) {
			httpUrlConnection.setRequestMethod("POST");
			httpUrlConnection.setDoOutput(true);
			OutputStream os = httpUrlConnection.getOutputStream();
			os.write(("data=").getBytes(charset));
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
			response = new JsonParser().parse(result);
			finished = true;
		} catch (Throwable t) {
			throw new IOException("Bad beacon response: `" + result + "`");
		}
	}
}
