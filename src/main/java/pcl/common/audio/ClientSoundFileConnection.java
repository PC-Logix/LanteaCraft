package pcl.common.audio;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import pcl.lc.LanteaCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class ClientSoundFileConnection extends URLConnection {

	private final ResourceLocation resourceName;

	private ClientSoundFileConnection(URL url) {
		super(url);
		this.resourceName = new ResourceLocation(url.getPath());
	}

	public void connect() {
	}

	public InputStream getInputStream() {
		try {
			LanteaCraft.getLogger().log(Level.INFO, "Looking for resource: " + resourceName.toString());
			InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(resourceName)
					.getInputStream();
			if (stream == null)
				throw new Exception("Could not open stream!");
			return stream;
		} catch (Exception ex) {
			LanteaCraft.getLogger().log(Level.WARNING, "Can't mount sound file!", ex);
			return null;
		}
	}

	ClientSoundFileConnection(URL url, ClientSoundProtocolHandler proto) {
		this(url);
	}

}
