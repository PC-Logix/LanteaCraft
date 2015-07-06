package lc.common.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import lc.api.components.ComponentType;
import lc.common.LCLog;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.configuration.xml.ComponentConfigList;
import lc.common.configuration.xml.ConfigHelper;
import lc.common.configuration.xml.XMLParser;
import lc.common.configuration.xml.XMLParserException;
import lc.common.configuration.xml.XMLSaver;
import lc.common.configuration.xml.XMLSaverException;
import lc.common.util.Tracer;
import lc.common.util.java.DeferredTaskExecutor;

/**
 * LanteaCraft configuration management controller.
 * 
 * @author AfterLifeLochie
 *
 */
public class ConfigurationController {

	private File workdir;

	private File defaultConfigFile;
	private ComponentConfigList defaultConfig;

	private Runnable writer = new Runnable() {
		@Override
		public void run() {
			commit();
		}
	};

	/** Default constructor */
	public ConfigurationController() {
		DeferredTaskExecutor.scheduleWithFixedDelay(writer, 240, 300, TimeUnit.SECONDS);
	}

	/**
	 * Initialize the configuration manager. The configuration manager will
	 * create the LanteaCraft storage directory at the path specified, then open
	 * and populate the default configuration file.1
	 * 
	 * @param wd
	 *            The current configuration working directory.
	 */
	public void initialize(File wd) {
		Tracer.begin(this);
		workdir = new File(wd, "LanteaCraft");
		if (!workdir.exists())
			workdir.mkdir();
		try {
			defaultConfigFile = new File(workdir, "config.xml");
			if (defaultConfigFile.exists()) {
				XMLParser parser = new XMLParser();
				FileInputStream test = new FileInputStream(defaultConfigFile);
				defaultConfig = parser.read(test);
				LCLog.debug("Loaded default configuration from disk.");
			} else {
				defaultConfig = new ComponentConfigList();
				LCLog.debug("No configuration on disk. Writing a new one.");
			}
		} catch (XMLParserException pex) {
			throw new RuntimeException("Error configuring LanteaCraft.", pex);
		} catch (IOException ioex) {
			throw new RuntimeException("Error configuring LanteaCraft setting storage.", ioex);
		} finally {
			Tracer.end();
		}
	}

	/**
	 * Commits the configuration to disk if any changes have been made to the
	 * configuration.
	 */
	public void commit() {
		if (defaultConfig.modified())
			try {
				Tracer.begin(this);
				LCLog.debug("Default configuration modified. Saving it...");
				XMLSaver saver = new XMLSaver();
				saver.save(defaultConfig, new FileOutputStream(defaultConfigFile));
				LCLog.debug("Default configuration committed.");
			} catch (XMLSaverException sex) {
				throw new RuntimeException("Error saving LanteaCraft configuration state.", sex);
			} catch (IOException ioex) {
				throw new RuntimeException("Error saving to LanteaCraft setting storage.", ioex);
			} finally {
				Tracer.end();
			}
	}

	/**
	 * Request the configuration controller provide a configuration container
	 * for a type of component category. If the component doesn't exist in the
	 * configuration, a new configuration node is created and returned instead.
	 * 
	 * @param type
	 *            The component type
	 * @return The configuration container for the component type
	 */
	public ComponentConfig config(ComponentType type) {
		return ConfigHelper.findComponentContainer(defaultConfig, type.name());
	}

	/**
	 * Get a File reference for a file name on the disk in the configuration
	 * directory.
	 * 
	 * @param name
	 *            The file path
	 * @return The fully-formed path to the configuration directory plus the
	 *         file path specified
	 */
	public File getObject(String name) {
		return new File(workdir, name);
	}

}
