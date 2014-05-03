package net.afterlifelochie.sandbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import pcl.common.xmlcfg.ConfigList;
import pcl.common.xmlcfg.ConfigNode;
import pcl.common.xmlcfg.ModuleConfig;
import pcl.common.xmlcfg.XMLParser;
import pcl.common.xmlcfg.XMLParserException;
import pcl.common.xmlcfg.XMLSaver;

public class LocalTest {

	public static void main(String[] args) {
		new LocalTest();
	}

	public LocalTest() {

		XMLParser parser = new XMLParser();
		XMLSaver saver = new XMLSaver();

		try {
			FileInputStream test = new FileInputStream(new File("LanteaCraft.xml"));
			ConfigList list = parser.read(test);

			for (ConfigNode node : list.children()) {

			}

		} catch (IOException ioex) {
			ioex.printStackTrace();
		} catch (XMLParserException parse) {
			parse.printStackTrace();
		}
	}

	public void printObject(ConfigNode node) {
		if (node instanceof ConfigList) {
			ConfigList list = (ConfigList) node;
			System.out.println(String.format("ConfigList: %s, %s", list.name(), list.comment()));
			for (ConfigNode child : list.children())
				printObject(child);
		} else if (node instanceof ModuleConfig) {
			ModuleConfig module = (ModuleConfig) node;
			System.out.println(String.format("ModuleConfig: %s, %s", module.name(), module.comment()));
			for (ConfigNode child : module.children())
				printObject(child);
		} else {
			System.out.println("");
		}
	}

}
