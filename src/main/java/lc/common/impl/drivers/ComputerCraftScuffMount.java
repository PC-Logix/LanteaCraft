package lc.common.impl.drivers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import lc.LanteaCraft;
import lc.common.LCLog;
import dan200.computercraft.api.filesystem.IMount;

/**
 * Disk and JAR mount. The vendor provided one doesn't work right, so here's a
 * re-implementation minus bugs.
 * 
 * @author AfterLifeLochie
 *
 */
public class ComputerCraftScuffMount implements IMount {

	public static ComputerCraftScuffMount generateMount() {
		String path = LanteaCraft.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (path.indexOf("!") >= 0)
			path = path.substring(0, path.indexOf("!"));
		if (path.endsWith(".class")) { /* expanded BIN */
			path = path.replace("lc/LanteaCraft.class", "");
		}
		LCLog.debug("Working path: %s", path);
		return new ComputerCraftScuffMount(new File(path), "assets/pcl_lc/drivers/support/computercraft");
	}

	/**
	 * Mounted file
	 * 
	 * @author AfterLifeLochie
	 *
	 */
	private class ScuffMappedFile {
		/** parent file */
		public ScuffMappedFile parent;
		/** disk filename */
		public String filename;
		/** disk subfiles */
		public HashMap<String, ScuffMappedFile> subfiles;

		/**
		 * Default constructor
		 * 
		 * @param filename
		 *            File safe name
		 */
		public ScuffMappedFile(String filename) {
			this.filename = filename;
		}

		public boolean isDirectory() {
			return this.subfiles != null;
		}

		public void file(ScuffMappedFile file) {
			if (subfiles == null)
				subfiles = new HashMap<String, ScuffMappedFile>();
			file.parent = this;
			subfiles.put(file.filename, file);
		}

		public ScuffMappedFile file(String name) {
			if (subfiles == null)
				return null;
			return subfiles.get(name);
		}
	}

	/** Target JAR file */
	private File mountFile;
	/** Target folder */
	private File mountFolder;
	/** Target subobject */
	private String mountSubFile;

	/** Compressed object I/O */
	private ZipFile mountDataArchive;
	/** Top of index */
	private ScuffMappedFile index;

	public ComputerCraftScuffMount(File file, String target) {
		if (file.isDirectory()) {
			mountFolder = new File(file, target); /* sec: merge them here */
		} else {
			mountFile = file; /* outer file */
			mountSubFile = target; /* inner file */
		}
	}

	public void init() throws ZipException, IOException {
		if (!isFilesystemMount())
			mountDataArchive = new ZipFile(mountFile);
		prepareDataIndex();
		if (!index.isDirectory())
			LCLog.warn("Failed to generate directory");
		if (index.subfiles.isEmpty())
			LCLog.warn("Didn't index any files");
	}

	public void shutdown() {
		try {
			if (mountDataArchive != null)
				mountDataArchive.close();
		} catch (IOException ioex) {
			LCLog.debug("Problem shutting down virtual filesystem.", ioex);
		} finally {
			mountDataArchive = null;
		}
	}

	public boolean isFilesystemMount() {
		return (mountFolder != null);
	}

	private void prepareDataIndex() throws IOException {
		/**
		 * FIXME: technically the top level file should have no name, but giving
		 * it slash so we can see where it's being misused.
		 */
		index = new ScuffMappedFile("/");
		if (isFilesystemMount()) {
			buildFileIndexInto(mountFolder, index);
		} else {
			ZipEntry top = mountDataArchive.getEntry(mountSubFile);
			if (top == null)
				throw new IOException("Unknown target folder " + mountSubFile);
			buildFileIndexInto(mountDataArchive, mountSubFile, index);
		}
	}

	private void buildFileIndexInto(File directory, ScuffMappedFile map) {
		File[] subfiles = directory.listFiles();
		for (File subfile : subfiles) {
			if (subfile.isDirectory()) {
				ScuffMappedFile dirMap = new ScuffMappedFile(subfile.getName());
				buildFileIndexInto(subfile, dirMap);
				map.file(dirMap);
			} else {
				map.file(new ScuffMappedFile(subfile.getName()));
			}
		}
	}

	private void buildFileIndexInto(ZipFile file, String topPath, ScuffMappedFile map) {
		Enumeration<? extends ZipEntry> iter = file.entries();
		while (iter.hasMoreElements()) {
			ZipEntry entry = iter.nextElement();
			String objectName = entry.getName();
			if (!objectName.startsWith(mountSubFile)) /* not in S(Q)? */
				continue; /* no, next */
			objectName = objectName.substring(mountSubFile.length()); /* ~S(Q) */
			String[] route = pathToScuff(objectName);

			/**
			 * Theoretically recurse all objects down the "path" until we reach
			 * the head; this will re-create the tree structure we expect to be
			 * working with normally
			 */
			ScuffMappedFile obj = index;
			for (int i = 0; i < route.length; i++) { /* each in p => */
				ScuffMappedFile obj0 = obj.file(route[i]);
				if (obj0 == null) { /* unknown? */
					obj0 = new ScuffMappedFile(route[i]);
					obj.file(obj0); /* make it so */
				}
				obj = obj0; /* jump to next */
			}
		}
	}

	private String[] pathToScuff(String path) {
		/**
		 * FIXME: check that computercraft sanitizes path before handing it to
		 * us; we need to make sure we handle ., .., ~, ... and some other weird
		 * bangs if not. Can do this by using a stack.
		 */
		if (path.startsWith("/")) /* lead slash? */
			path = path.substring(1); /* no lead */
		if (path.endsWith("/")) /* trail slash? */
			path = path.substring(0, path.length() - 1); /* no trail */
		String[] bits = path.trim().split("/"); /* break it */
		ArrayList<String> result = new ArrayList<String>();
		for (String bit : bits)
			if (bit.length() != 0)
				result.add(bit);
		return result.toArray(new String[0]);
	}

	private ScuffMappedFile fileForPath(String path) {
		String[] route = pathToScuff(path);
		ScuffMappedFile obj = index;
		/**
		 * Just walk the tree. If we request a name in the tree which doesn't
		 * exist, we get null from the map and return null instead.
		 */
		for (int i = 0; i < route.length; i++) {
			obj = obj.file(route[i]);
			if (obj == null)
				return null;
		}
		return obj;
	}

	private String getStringForFile(ScuffMappedFile file) {
		Stack<String> stack = new Stack<String>();
		ScuffMappedFile file0 = file;
		while (file0 != null) { /* for all in parent */
			stack.push(file0.filename); /* push name */
			file0 = file0.parent; /* next parent */
			if (file0 == index) /* at top level file? */
				break;
		}
		if (file0 == null) /* not at top? */
			throw new RuntimeException("Expected to arrive at index but did not.");
		StringBuilder blob = new StringBuilder();
		while (!stack.isEmpty())
			blob.append("/").append(stack.pop()); /* flatten gathered names */
		return blob.toString();
	}

	@Override
	public boolean exists(String path) throws IOException {
		return fileForPath(path) != null; /* not indexed? */
	}

	@Override
	public boolean isDirectory(String path) throws IOException {
		ScuffMappedFile file = fileForPath(path);
		if (file == null) /* not indexed? */
			return false;
		return file.isDirectory();
	}

	@Override
	public void list(String path, List<String> contents) throws IOException {
		/**
		 * Assuming we need only return names here and that directories are
		 * automatically detected, so not returning trailing slash on
		 * directories; could be wrong.
		 */
		ScuffMappedFile file = fileForPath(path);
		if (file == null || file.subfiles == null) /* not indexed? */
			return;
		for (String filename : file.subfiles.keySet())
			contents.add(filename);
	}

	@Override
	public long getSize(String path) throws IOException {
		/* FIXME: this is a guessed method body */
		ScuffMappedFile file = fileForPath(path);
		if (file == null) /* not indexed? */
			return 0L;
		String realpath = getStringForFile(file);
		if (isFilesystemMount()) {
			File f0 = new File(mountFolder, realpath);
			if (f0.isDirectory())
				return 0L;
			return f0.length();
		} else {
			ZipEntry f0 = mountDataArchive.getEntry(mountSubFile + realpath);
			if (f0.isDirectory())
				return 0L;
			return f0.getSize();
		}
	}

	@Override
	public InputStream openForRead(String path) throws IOException {
		/* FIXME: this is a guessed method body */
		ScuffMappedFile file = fileForPath(path);
		if (file == null || file.isDirectory()) /* not indexed? */
			throw new IOException("No such file");
		String realpath = getStringForFile(file);
		if (isFilesystemMount()) {
			File f0 = new File(mountFolder, realpath);
			return new FileInputStream(f0);
		} else {
			ZipEntry f0 = mountDataArchive.getEntry(mountSubFile + realpath);
			return mountDataArchive.getInputStream(f0);
		}
	}

}
