package pcl.common.audio;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.apache.logging.log4j.Level;

import pcl.lc.BuildInfo;
import pcl.lc.LanteaCraft;

public class ClientSoundProtocolHandler extends URLStreamHandler {

	@Override
	protected URLConnection openConnection(URL par1URL) {
		if (BuildInfo.SS_DEBUGGING)
			LanteaCraft.getLogger().log(Level.INFO, "SoundConnection opening: " + par1URL.toString());
		return new ClientSoundFileConnection(par1URL, (ClientSoundProtocolHandler) null);
	}

}
