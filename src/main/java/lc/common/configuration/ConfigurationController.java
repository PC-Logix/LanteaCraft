package lc.common.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import lc.api.components.ComponentType;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.configuration.xml.ComponentConfigList;
import lc.common.configuration.xml.ConfigHelper;
import lc.common.configuration.xml.XMLParser;
import lc.common.configuration.xml.XMLParserException;
import lc.common.configuration.xml.XMLSaver;
import lc.common.configuration.xml.XMLSaverException;

public class ConfigurationController {

	private File workdir;

	private File defaultConfigFile;
	private ComponentConfigList defaultConfig;

	public ConfigurationController() {
	}

	public void initialize(File wd) {
		workdir = new File(wd, "LanteaCraft");
		if (!workdir.exists())
			workdir.mkdir();
		try {
			defaultConfigFile = new File(workdir, "config.xml");
			if (defaultConfigFile.exists()) {
				XMLParser parser = new XMLParser();
				FileInputStream test = new FileInputStream(defaultConfigFile);
				defaultConfig = parser.read(test);
			} else
				defaultConfig = new ComponentConfigList();
		} catch (XMLParserException pex) {
			throw new RuntimeException("Error configuring LanteaCraft.", pex);
		} catch (IOException ioex) {
			throw new RuntimeException("Error configuring LanteaCraft setting storage.", ioex);
		}
	}

	public void commit() {
		if (defaultConfig.modified())
			try {
				XMLSaver saver = new XMLSaver();
				saver.save(defaultConfig, new FileOutputStream(defaultConfigFile));
			} catch (XMLSaverException sex) {
				throw new RuntimeException("Error saving LanteaCraft configuration state.", sex);
			} catch (IOException ioex) {
				throw new RuntimeException("Error saving to LanteaCraft setting storage.", ioex);
			}
	}

	public ComponentConfig config(ComponentType type) {
		return ConfigHelper.findComponentContainer(defaultConfig, type.name());
	}

	public File getObject(String name) {
		return new File(workdir, name);
	}

}
