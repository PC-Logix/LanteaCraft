package pcl.common.audio;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.logging.Level;

import pcl.lc.LanteaCraft;

public class ClientSoundProtocolHandler extends URLStreamHandler {

	protected URLConnection openConnection(URL par1URL) {
		LanteaCraft.getLogger().log(Level.INFO, "SoundConnection opening: " + par1URL.toString());
		return new ClientSoundFileConnection(par1URL, (ClientSoundProtocolHandler) null);
	}

}
