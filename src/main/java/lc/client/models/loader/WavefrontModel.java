package lc.client.models.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

public class WavefrontModel {

	public static class WavefrontModelException extends Exception {
		public WavefrontModelException(String reason) {
			super(reason);
		}

		public WavefrontModelException(String reason, Throwable top) {
			super(reason, top);
		}

		public WavefrontModelException(String reason, int idx, String line) {
			super(String.format("[line %s]: %s (`%s`)", idx, reason, line));
		}
	}

	public static class Face {
		public Vertex[] vertices;
		public TextureCoord[] texCoords;
		public Vertex[] normals;
		public Vertex faceNormal;

		public Face() {
		}

		public Vertex calculateFaceNormal() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class TextureCoord {
		public TextureCoord(float u, float v) {
			// TODO Auto-generated constructor stub
		}

		public TextureCoord(float u, float v, float x) {
			// TODO Auto-generated constructor stub
		}
	}

	public static class Vertex {
		public Vertex(float x, float y, float z) {
			// TODO Auto-generated constructor stub
		}

		public Vertex(float x, float y) {
			// TODO Auto-generated constructor stub
		}
	}

	public static class ElementGroup {
		public final ArrayList<Face> faces = new ArrayList<Face>();
		public int glDrawingMode;

		public ElementGroup(String label) {
			// TODO Auto-generated constructor stub
		}
	}

	private static Pattern ruleVertex = Pattern
			.compile("(v( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(v( (\\-){0,1}\\d+\\.\\d+){3,4} *$)");
	private static Pattern ruleNormal = Pattern
			.compile("(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *$)");
	private static Pattern ruleTexCoord = Pattern
			.compile("(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *\\n)|(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *$)");
	private static Pattern ruleFace_V_VT_VN = Pattern
			.compile("(f( \\d+/\\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+/\\d+){3,4} *$)");
	private static Pattern ruleFace_V_VT = Pattern.compile("(f( \\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+){3,4} *$)");
	private static Pattern ruleFace_V_VN = Pattern.compile("(f( \\d+//\\d+){3,4} *\\n)|(f( \\d+//\\d+){3,4} *$)");
	private static Pattern ruleFace_V = Pattern.compile("(f( \\d+){3,4} *\\n)|(f( \\d+){3,4} *$)");
	private static Pattern ruleObjGroup = Pattern.compile("([go]( [\\w\\d]+) *\\n)|([go]( [\\w\\d]+) *$)");

	public final String name;
	public final ArrayList<Vertex> vertexHeap = new ArrayList<Vertex>();
	public final ArrayList<Vertex> normalHeap = new ArrayList<Vertex>();
	public final ArrayList<TextureCoord> texCoordHeap = new ArrayList<TextureCoord>();
	public final ArrayList<ElementGroup> groupHeap = new ArrayList<ElementGroup>();

	public WavefrontModel(String name, InputStream input) {
		this.name = name;
	}

	private void loadObjModel(InputStream inputStream) throws WavefrontModelException {
		BufferedReader reader = null;
		String currentLine = null;
		int lineCount = 0;
		ElementGroup currentGroupObject = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream));
			while ((currentLine = reader.readLine()) != null) {
				lineCount++;
				currentLine = currentLine.replaceAll("\\s+", " ").trim();

				if (currentLine.startsWith("#") || currentLine.length() == 0) {
					continue;
				} else if (currentLine.startsWith("v ")) {
					Vertex vertex = ofVertex(currentLine, lineCount);
					if (vertex != null)
						vertexHeap.add(vertex);
				} else if (currentLine.startsWith("vn ")) {
					Vertex vertex = ofNormal(currentLine, lineCount);
					if (vertex != null)
						normalHeap.add(vertex);
				} else if (currentLine.startsWith("vt ")) {
					TextureCoord textureCoordinate = ofTexCoord(currentLine, lineCount);
					if (textureCoordinate != null)
						texCoordHeap.add(textureCoordinate);
				} else if (currentLine.startsWith("f ")) {
					if (currentGroupObject == null)
						currentGroupObject = new ElementGroup("Default");
					Face face = ofFace(currentLine, lineCount, currentGroupObject);
					if (face != null)
						currentGroupObject.faces.add(face);
				} else if (currentLine.startsWith("g ") | currentLine.startsWith("o ")) {
					ElementGroup group = ofElementGroup(currentLine, lineCount);
					if (group != null)
						if (currentGroupObject != null)
							groupHeap.add(currentGroupObject);
					currentGroupObject = group;
				}
			}

			groupHeap.add(currentGroupObject);
		} catch (IOException e) {
			throw new WavefrontModelException("Stream error.", e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			}

			try {
				inputStream.close();
			} catch (IOException e) {
			}
		}
	}

	private ElementGroup ofElementGroup(String line, int idx) throws WavefrontModelException {
		if (ruleObjGroup.matcher(line).matches()) {
			ElementGroup group = null;
			String trimmedLine = line.substring(line.indexOf(" ") + 1);
			if (trimmedLine.length() > 0)
				group = new ElementGroup(trimmedLine);
			return group;
		} else
			throw new WavefrontModelException("Not a valid element group.", idx, line);
	}

	private Face ofFace(String line, int idx, ElementGroup group) throws WavefrontModelException {
		if (ruleFace_V_VT_VN.matcher(line).matches() || ruleFace_V_VT.matcher(line).matches()
				|| ruleFace_V_VN.matcher(line).matches() || ruleFace_V.matcher(line).matches()) {
			Face face = new Face();
			String trimmedLine = line.substring(line.indexOf(" ") + 1);
			String[] tokens = trimmedLine.split(" ");
			String[] subTokens = null;

			if (tokens.length == 3) {
				if (group.glDrawingMode == -1) {
					group.glDrawingMode = GL11.GL_TRIANGLES;
				} else if (group.glDrawingMode != GL11.GL_TRIANGLES)
					throw new WavefrontModelException(String.format("Invalid points for face: expected 4, got %s.",
							tokens.length), idx, line);
			} else if (tokens.length == 4) {
				if (group.glDrawingMode == -1) {
					group.glDrawingMode = GL11.GL_QUADS;
				} else if (group.glDrawingMode != GL11.GL_QUADS)
					throw new WavefrontModelException(String.format("Invalid points for face: expected 3, got %s.",
							tokens.length), idx, line);
			}

			if (ruleFace_V_VT_VN.matcher(line).matches()) {
				face.vertices = new Vertex[tokens.length];
				face.texCoords = new TextureCoord[tokens.length];
				face.normals = new Vertex[tokens.length];
				for (int i = 0; i < tokens.length; ++i) {
					subTokens = tokens[i].split("/");
					face.vertices[i] = vertexHeap.get(Integer.parseInt(subTokens[0]) - 1);
					face.texCoords[i] = texCoordHeap.get(Integer.parseInt(subTokens[1]) - 1);
					face.normals[i] = normalHeap.get(Integer.parseInt(subTokens[2]) - 1);
				}

				face.faceNormal = face.calculateFaceNormal();
			} else if (ruleFace_V_VT.matcher(line).matches()) {
				face.vertices = new Vertex[tokens.length];
				face.texCoords = new TextureCoord[tokens.length];
				for (int i = 0; i < tokens.length; ++i) {
					subTokens = tokens[i].split("/");
					face.vertices[i] = vertexHeap.get(Integer.parseInt(subTokens[0]) - 1);
					face.texCoords[i] = texCoordHeap.get(Integer.parseInt(subTokens[1]) - 1);
				}

				face.faceNormal = face.calculateFaceNormal();
			} else if (ruleFace_V_VN.matcher(line).matches()) {
				face.vertices = new Vertex[tokens.length];
				face.normals = new Vertex[tokens.length];
				for (int i = 0; i < tokens.length; ++i) {
					subTokens = tokens[i].split("//");
					face.vertices[i] = vertexHeap.get(Integer.parseInt(subTokens[0]) - 1);
					face.normals[i] = normalHeap.get(Integer.parseInt(subTokens[1]) - 1);
				}

				face.faceNormal = face.calculateFaceNormal();
			} else if (ruleFace_V.matcher(line).matches()) {
				face.vertices = new Vertex[tokens.length];
				for (int i = 0; i < tokens.length; ++i)
					face.vertices[i] = vertexHeap.get(Integer.parseInt(tokens[i]) - 1);
				face.faceNormal = face.calculateFaceNormal();
			} else {
				throw new WavefrontModelException("Unknown instruction on line.", idx, line);
			}

			return face;
		} else
			throw new WavefrontModelException("Unsupported face format.", idx, line);
	}

	private TextureCoord ofTexCoord(String line, int idx) throws WavefrontModelException {
		if (ruleTexCoord.matcher(line).matches()) {
			TextureCoord textureCoordinate = null;
			line = line.substring(line.indexOf(" ") + 1);
			String[] tokens = line.split(" ");
			try {
				if (tokens.length == 2)
					return new TextureCoord(Float.parseFloat(tokens[0]), 1 - Float.parseFloat(tokens[1]));
				else if (tokens.length == 3)
					return new TextureCoord(Float.parseFloat(tokens[0]), 1 - Float.parseFloat(tokens[1]),
							Float.parseFloat(tokens[2]));
			} catch (NumberFormatException e) {
				throw new WavefrontModelException(String.format("Number formatting error at line %d.", idx), e);
			}
			return textureCoordinate;
		} else
			throw new WavefrontModelException("Not a valid texture coordinate.", idx, line);

	}

	private Vertex ofNormal(String line, int idx) throws WavefrontModelException {

		if (ruleNormal.matcher(line).matches()) {
			line = line.substring(line.indexOf(" ") + 1);
			String[] tokens = line.split(" ");
			try {
				if (tokens.length == 3)
					return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]),
							Float.parseFloat(tokens[2]));
				return null;
			} catch (NumberFormatException e) {
				throw new WavefrontModelException(String.format("Number formatting error at line %d.", idx), e);
			}
		} else
			throw new WavefrontModelException("Not a valid normal vertex.", idx, line);
	}

	private Vertex ofVertex(String line, int idx) throws WavefrontModelException {
		if (ruleVertex.matcher(line).matches()) {
			line = line.substring(line.indexOf(" ") + 1);
			String[] tokens = line.split(" ");
			try {
				if (tokens.length == 2)
					return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]));
				else if (tokens.length == 3)
					return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]),
							Float.parseFloat(tokens[2]));
				return null;
			} catch (NumberFormatException e) {
				throw new WavefrontModelException(String.format("Number formatting error at line %d.", idx), e);
			}
		} else
			throw new WavefrontModelException("Not a valid vertex.", idx, line);
	}

}
