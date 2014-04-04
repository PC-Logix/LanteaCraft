package pcl.lc.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;

import pcl.lc.LanteaCraft;

/**
 * Logfile on a per world basis.
 * 
 * @author AfterLifeLochie
 * 
 */
public class WorldLog {

	private final File file;
	private FileOutputStream outstream;
	private PrintStream outwrapper;

	public WorldLog(File file) {
		this.file = file;
	}

	public void open() {
		try {
			if (outstream != null || outwrapper != null)
				throw new IOException("Can't reopen an existing WorldLog.");
			outstream = new FileOutputStream(file, true);
			outwrapper = new PrintStream(outstream);
		} catch (IOException ioex) {
			LanteaCraft.getLogger().log(Level.WARNING, "Could not create WorldLog, an error occured.", ioex);
		}
	}

	public void close() {
		try {
			if (outwrapper != null) {
				outwrapper.flush();
				outwrapper.close();
			}
			if (outstream != null) {
				outstream.flush();
				outstream.close();
			}
		} catch (IOException ioex) {
			LanteaCraft.getLogger().log(Level.WARNING, "Could not close WorldLog, an error occured.", ioex);
		}
	}

	public void log(Level level, String message) {
		try {
			if (outwrapper == null)
				return;
			StringBuilder formatter = new StringBuilder();
			formatter.append("[").append(level).append("]");
			formatter.append(" ").append(message);
			outwrapper.println(formatter.toString());
		} catch (Throwable ioex) {
			LanteaCraft.getLogger().log(Level.WARNING, "Could not write to WorldLog, an error occured.", ioex);
		}
	}

}
