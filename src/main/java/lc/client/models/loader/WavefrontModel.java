package lc.client.models.loader;

import java.io.InputStream;

public class WavefrontModel {

	public static class WavefrontModelException extends Exception {
		public WavefrontModelException(String reason) {
			super(reason);
		}

		public WavefrontModelException(String reason, Throwable top) {
			super(reason, top);
		}
	}

	public static class Face {
		public Face() {
		}
	}

	public static class TextureCoord {
		public TextureCoord() {
		}
	}

	public static class Vertex {
		public Vertex() {
		}
	}

	public static class ElementGroup {
		public ElementGroup() {
		}
	}

	public WavefrontModel(String name, InputStream input) {

	}

}
