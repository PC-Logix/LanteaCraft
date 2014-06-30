package pcl.common.audio;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import pcl.lc.BuildInfo;
import pcl.lc.LanteaCraft;

public class ClientSoundFileConnection extends URLConnection {

	private final ResourceLocation resourceName;

	private ClientSoundFileConnection(URL url) {
		super(url);
		resourceName = new ResourceLocation(url.getPath());
	}

	@Override
	public void connect() {
	}

	@Override
	public InputStream getInputStream() {
		try {
			if (BuildInfo.SS_DEBUGGING)
				LanteaCraft.getLogger().log(Level.INFO, "Looking for resource: " + resourceName.toString());
			InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(resourceName)
					.getInputStream();
			if (stream == null)
				throw new Exception("Could not open stream!");
			return stream;
		} catch (Exception ex) {
			LanteaCraft.getLogger().log(Level.WARN, "Can't mount sound file!", ex);
			return null;
		}
	}

	ClientSoundFileConnection(URL url, ClientSoundProtocolHandler proto) {
		this(url);
	}

}
