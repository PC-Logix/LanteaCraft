package lc.client.openal.io;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import lc.common.LCLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

public class StreamingSoundFileConnection extends URLConnection {

	private final ResourceLocation resourceName;
	private final StreamingSoundProtocolHandler handler;

	public StreamingSoundFileConnection(URL url, StreamingSoundProtocolHandler proto) {
		super(url);
		resourceName = new ResourceLocation(url.getPath());
		this.handler = proto;
	}

	@Override
	public void connect() {
	}

	@Override
	public InputStream getInputStream() {
		try {
			LCLog.debug("Looking for resource: %s", resourceName.toString());
			IResource rsrc = Minecraft.getMinecraft().getResourceManager().getResource(resourceName);
			InputStream stream = rsrc.getInputStream();
			if (stream == null)
				throw new NullPointerException("stream == null!");
			return stream;
		} catch (Exception ex) {
			LCLog.warn("Can't open sound stream", ex);
			return null;
		}
	}
}
