package lc.client.openal.io;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import lc.common.LCLog;

public class StreamingSoundProtocolHandler extends URLStreamHandler {
	@Override
	protected URLConnection openConnection(URL uri) {
		LCLog.debug("StreamingSoundProtocolHandler opening: %s", uri.toString());
		return new StreamingSoundFileConnection(uri, this);
	}
}
