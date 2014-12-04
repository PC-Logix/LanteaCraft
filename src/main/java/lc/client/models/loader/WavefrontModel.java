package lc.client.models.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

/**
 * Wavefront model container. Originally from FML, apparently being removed from
 * FML due to Minecraft's new 'model format'.
 *
 * FIXME: This needs to be rewritten as a proper lexer-parser combo. Using
 * patterns is exceptionally expensive (even when they're cached) which is the
 * exact reason *why* we use parser tools (such as look-ahead).
 *
 * @author AfterLifeLochie, LexManos, AbrarSyed, pahimar, cpw
 *
 */
public class WavefrontModel {

	/**
	 * Wavefront model exception container.
	 *
	 * @author AfterLifeLochie
	 */
	public static class WavefrontModelException extends Exception {
		/** Serializer ID */
		private static final long serialVersionUID = 5032918858527482568L;

		/**
		 * @param reason
		 *            The reason for the exception
		 */
		public WavefrontModelException(String reason) {
			super(reason);
		}

		/**
		 * @param reason
		 *            The reason for the exception
		 * @param top
		 *            The cause exception
		 */
		public WavefrontModelException(String reason, Throwable top) {
			super(reason, top);
		}

		/**
		 * @param reason
		 *            The reason for the exception
		 * @param idx
		 *            The line number
		 * @param line
		 *            The line text
		 */
		public WavefrontModelException(String reason, int idx, String line) {
			super(String.format("[line %s]: %s (`%s`)", idx, reason, line));
		}
	}

	/**
	 * Model vertex container.
	 *
	 * @author AfterLifeLochie, LexManos, AbrarSyed, pahimar
	 */
	public static class Vertex {
		public float x, y, z;

		/**
		 * @param x
		 *            The x-coord of the vertex
		 * @param y
		 *            The y-coord of the vertex
		 */
		public Vertex(float x, float y) {
			this(x, y, 0F);
		}

		/**
		 * @param x
		 *            The x-coord of the vertex
		 * @param y
		 *            The y-coord of the vertex
		 * @param z
		 *            The z-coord of the vertex
		 */
		public Vertex(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	/**
	 * Texture coordinate container
	 * 
	 * @author AfterLifeLochie, LexManos, AbrarSyed, pahimar
	 */
	public static class TextureCoord {
		public float u, v, w;

		/**
		 * @param u
		 *            The u-coord of the texture
		 * @param v
		 *            The v-coord of the texture
		 */
		public TextureCoord(float u, float v) {
			this(u, v, 0F);
		}

		/**
		 * @param u
		 *            The u-coord of the texture
		 * @param v
		 *            The v-coord of the texture
		 * @param w
		 *            The w-coord of the texture
		 */
		public TextureCoord(float u, float v, float w) {
			this.u = u;
			this.v = v;
			this.w = w;
		}
	}

	/**
	 * Model face container.
	 *
	 * @author AfterLifeLochie, LexManos, AbrarSyed, pahimar
	 */
	public static class Face {
		/** The list of verticies */
		public Vertex[] vertices;
		/** The list of face normals */
		public Vertex[] normals;
		/** The list of texture coordinates */
		public TextureCoord[] texCoords;
		/** The global face normal */
		public Vertex faceNormal;

		/** Default constructor */
		public Face() {
		}

		/**
		 * Add the face for rendering
		 * 
		 * @param tessellator
		 *            The tessellator to write to
		 */
		public void addFaceForRender(Tessellator tessellator) {
			addFaceForRender(tessellator, 0.0005F);
		}

		/**
		 * Add the face for rendering
		 * 
		 * @param tessellator
		 *            The tessellator to write to
		 * @param textureOffset
		 *            The texture coordinate offset
		 */
		public void addFaceForRender(Tessellator tessellator, float textureOffset) {
			if (faceNormal == null)
				faceNormal = calculateFaceNormal();
			tessellator.setNormal(faceNormal.x, faceNormal.y, faceNormal.z);

			float averageU = 0F, averageV = 0F;
			if (texCoords != null && texCoords.length > 0) {
				for (TextureCoord texCoord : texCoords) {
					averageU += texCoord.u;
					averageV += texCoord.v;
				}
				averageU = averageU / texCoords.length;
				averageV = averageV / texCoords.length;
			}

			float offsetU, offsetV;

			for (int i = 0; i < vertices.length; ++i)
				if (texCoords != null && texCoords.length > 0) {
					offsetU = textureOffset;
					offsetV = textureOffset;
					if (texCoords[i].u > averageU)
						offsetU = -offsetU;
					if (texCoords[i].v > averageV)
						offsetV = -offsetV;
					tessellator.addVertexWithUV(vertices[i].x, vertices[i].y, vertices[i].z, texCoords[i].u + offsetU,
							texCoords[i].v + offsetV);
				} else
					tessellator.addVertex(vertices[i].x, vertices[i].y, vertices[i].z);
		}

		/**
		 * Calculate the face normal of the face
		 * 
		 * @return The face normal value
		 */
		public Vertex calculateFaceNormal() {
			Vec3 v1 = Vec3.createVectorHelper(vertices[1].x - vertices[0].x, vertices[1].y - vertices[0].y,
					vertices[1].z - vertices[0].z);
			Vec3 v2 = Vec3.createVectorHelper(vertices[2].x - vertices[0].x, vertices[2].y - vertices[0].y,
					vertices[2].z - vertices[0].z);
			Vec3 normalVector = v1.crossProduct(v2).normalize();
			return new Vertex((float) normalVector.xCoord, (float) normalVector.yCoord, (float) normalVector.zCoord);
		}
	}

	/**
	 * Model element group container.
	 *
	 * @author AfterLifeLochie, LexManos, AbrarSyed, pahimar
	 */
	public static class ElementGroup {
		/** The name of the group */
		public final String name;
		/** The list of faces */
		public final ArrayList<Face> faces = new ArrayList<Face>();
		/** The opengl drawing mode for this group */
		public int glDrawingMode;

		/** Default constructor */
		public ElementGroup() {
			this("");
		}

		/**
		 * Creates a new element group
		 * 
		 * @param name
		 *            The name of the group
		 */
		public ElementGroup(String name) {
			this(name, -1);
		}

		/**
		 * Creates a new element group
		 * 
		 * @param name
		 *            The name of the group
		 * @param glDrawingMode
		 *            The drawing mode
		 */
		public ElementGroup(String name, int glDrawingMode) {
			this.name = name;
			this.glDrawingMode = glDrawingMode;
		}

		/**
		 * Render the element group
		 */
		public void render() {
			if (faces.size() > 0) {
				Tessellator tessellator = Tessellator.instance;
				tessellator.startDrawing(glDrawingMode);
				render(tessellator);
				tessellator.draw();
			}
		}

		/**
		 * Render the element group
		 * 
		 * @param tessellator
		 *            The tessellator to write to
		 */
		public void render(Tessellator tessellator) {
			if (faces.size() > 0)
				for (Face face : faces)
					face.addFaceForRender(tessellator);
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

	/** The model name */
	public final String name;
	/** If the model is ready */
	public boolean ready = false;
	/** The stored vertex heap */
	public final ArrayList<Vertex> vertexHeap = new ArrayList<Vertex>();
	/** The stored vertex normal heap */
	public final ArrayList<Vertex> normalHeap = new ArrayList<Vertex>();
	/** The stored texture coordinate heap */
	public final ArrayList<TextureCoord> texCoordHeap = new ArrayList<TextureCoord>();
	/** The stored element group heap */
	public final ArrayList<ElementGroup> groupHeap = new ArrayList<ElementGroup>();
	/** The last element group written */
	public ElementGroup lastGroup;

	/**
	 * Create and load a new Wavefront Model.
	 *
	 * @param resource
	 *            The ResourceLocation of the model file.
	 * @throws WavefrontModelException
	 *             Any parse exception.
	 */
	public WavefrontModel(ResourceLocation resource) throws WavefrontModelException {
		try {
			name = resource.toString();
			loadObjModel(Minecraft.getMinecraft().getResourceManager().getResource(resource).getInputStream());
		} catch (IOException ex) {
			throw new WavefrontModelException("Can't read input model file.", ex);
		}
	}

	/**
	 * Create and load a new Wavefront Model.
	 *
	 * @param name
	 *            The file name.
	 * @param input
	 *            The input stream
	 * @throws WavefrontModelException
	 *             Any parse exception.
	 */
	public WavefrontModel(String name, InputStream input) throws WavefrontModelException {
		this.name = name;
		loadObjModel(input);
	}

	/** Render all elements in the model */
	public void renderAll() {
		Tessellator tessellator = Tessellator.instance;
		if (lastGroup != null)
			tessellator.startDrawing(lastGroup.glDrawingMode);
		else
			tessellator.startDrawing(GL11.GL_TRIANGLES);
		tessellateAll(tessellator);
		tessellator.draw();
	}

	/**
	 * Tessellate all elements in the model
	 * 
	 * @param tessellator
	 *            The tessellator to write to
	 */
	public void tessellateAll(Tessellator tessellator) {
		for (ElementGroup groupObject : groupHeap)
			groupObject.render(tessellator);
	}

	/**
	 * Render only the elements which match the group name(s)
	 * 
	 * @param groupNames
	 *            The group names
	 */
	public void renderOnly(String... groupNames) {
		for (ElementGroup groupObject : groupHeap)
			for (String groupName : groupNames)
				if (groupName.equalsIgnoreCase(groupObject.name))
					groupObject.render();
	}

	/**
	 * Tessellate only the elements which match the group name(s)
	 * 
	 * @param tessellator
	 *            The tessellator to write to
	 * @param groupNames
	 *            The group names
	 */
	public void tessellateOnly(Tessellator tessellator, String... groupNames) {
		for (ElementGroup groupObject : groupHeap)
			for (String groupName : groupNames)
				if (groupName.equalsIgnoreCase(groupObject.name))
					groupObject.render(tessellator);
	}

	/**
	 * Render a single part
	 * 
	 * @param partName
	 *            The part name
	 */
	public void renderPart(String partName) {
		for (ElementGroup groupObject : groupHeap)
			if (partName.equalsIgnoreCase(groupObject.name))
				groupObject.render();
	}

	/**
	 * Tessellate a single part
	 * 
	 * @param tessellator
	 *            The tessellator to write to
	 * @param partName
	 *            The part name
	 */
	public void tessellatePart(Tessellator tessellator, String partName) {
		for (ElementGroup groupObject : groupHeap)
			if (partName.equalsIgnoreCase(groupObject.name))
				groupObject.render(tessellator);
	}

	/**
	 * Render all elements except those in a list of groups
	 * 
	 * @param excludedGroupNames
	 *            The list of groups
	 */
	public void renderAllExcept(String... excludedGroupNames) {
		for (ElementGroup groupObject : groupHeap) {
			boolean flag = false;
			for (String excludedGroupName : excludedGroupNames)
				if (excludedGroupName.equalsIgnoreCase(groupObject.name))
					flag = true;
			if (!flag)
				groupObject.render();
		}
	}

	/**
	 * Tessellate all elements except those in a list of groups
	 * 
	 * @param tessellator
	 *            The tessellator to write to
	 * @param excludedGroupNames
	 *            The list of groups
	 */
	public void tessellateAllExcept(Tessellator tessellator, String... excludedGroupNames) {
		for (ElementGroup groupObject : groupHeap) {
			boolean flag = false;
			for (String excludedGroupName : excludedGroupNames)
				if (excludedGroupName.equalsIgnoreCase(groupObject.name))
					flag = true;
			if (!flag)
				groupObject.render(tessellator);
		}
	}

	private void loadObjModel(InputStream inputStream) throws WavefrontModelException {
		BufferedReader reader = null;
		String currentLine = null;
		int lineCount = 0;
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream));
			while ((currentLine = reader.readLine()) != null) {
				lineCount++;
				currentLine = currentLine.replaceAll("\\s+", " ").trim();

				if (currentLine.startsWith("#") || currentLine.length() == 0)
					continue;
				else if (currentLine.startsWith("v ")) {
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
					if (lastGroup == null)
						lastGroup = new ElementGroup("Default");
					Face face = ofFace(currentLine, lineCount);
					if (face != null)
						lastGroup.faces.add(face);
				} else if (currentLine.startsWith("g ") | currentLine.startsWith("o ")) {
					ElementGroup group = ofElementGroup(currentLine, lineCount);
					if (group != null)
						if (lastGroup != null)
							groupHeap.add(lastGroup);
					lastGroup = group;
				}
			}

			groupHeap.add(lastGroup);
			ready = true;
		} catch (IOException e) {
			throw new WavefrontModelException("Stream error.", e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				/* Do nothing */
			}

			try {
				inputStream.close();
			} catch (IOException e) {
				/* Do nothing */
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

	private Face ofFace(String line, int idx) throws WavefrontModelException {
		if (ruleFace_V_VT_VN.matcher(line).matches() || ruleFace_V_VT.matcher(line).matches()
				|| ruleFace_V_VN.matcher(line).matches() || ruleFace_V.matcher(line).matches()) {
			Face face = new Face();
			String trimmedLine = line.substring(line.indexOf(" ") + 1);
			String[] tokens = trimmedLine.split(" ");
			String[] subTokens = null;

			if (tokens.length == 3) {
				if (lastGroup.glDrawingMode == -1)
					lastGroup.glDrawingMode = GL11.GL_TRIANGLES;
				else if (lastGroup.glDrawingMode != GL11.GL_TRIANGLES)
					throw new WavefrontModelException(String.format("Invalid points for face: expected 4, got %s.",
							tokens.length), idx, line);
			} else if (tokens.length == 4)
				if (lastGroup.glDrawingMode == -1)
					lastGroup.glDrawingMode = GL11.GL_QUADS;
				else if (lastGroup.glDrawingMode != GL11.GL_QUADS)
					throw new WavefrontModelException(String.format("Invalid points for face: expected 3, got %s.",
							tokens.length), idx, line);

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
			} else
				throw new WavefrontModelException("Unknown instruction on line.", idx, line);

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
