package net.afterlifelochie.sandbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import pcl.common.xmlcfg.ConfigList;
import pcl.common.xmlcfg.ConfigNode;
import pcl.common.xmlcfg.ModuleConfig;
import pcl.common.xmlcfg.ModuleList;
import pcl.common.xmlcfg.XMLParser;
import pcl.common.xmlcfg.XMLParserException;
import pcl.common.xmlcfg.XMLSaver;
import pcl.common.xmlcfg.XMLSaverException;

public class LocalTest {

	public static void main(String[] args) {
		new LocalTest();
	}

	public LocalTest() {

		XMLParser parser = new XMLParser();
		XMLSaver saver = new XMLSaver();

		try {
			FileInputStream test = new FileInputStream(new File("LanteaCraft.xml"));
			ModuleList list = parser.read(test);
			printObject(0, list);
			
			for (ModuleConfig module : list.children()) {
				if (module.name().equalsIgnoreCase("core")) {
					ConfigNode newNode = new ConfigNode("doYouLikeBacon", "Bacon, yes?", module);
					newNode.parameters().put("yummy", "yes");
					module.children().add(newNode);
					newNode.modify();
				}
			}
			System.out.println(String.format("Root modified: %s", list.modified()));
			saver.save(list, new FileOutputStream(new File("LanteaCraft-out.xml")));

		} catch (IOException ioex) {
			ioex.printStackTrace();
		} catch (XMLParserException parse) {
			parse.printStackTrace();
		} catch (XMLSaverException save) {
			save.printStackTrace();
		}
	}

	public void printObject(int level, ConfigNode node) {
		StringBuilder tabs = new StringBuilder();
		for (int i = 0; i < level; i++)
			tabs.append("\t");
		if (node instanceof ModuleList) {
			ModuleList list = (ModuleList) node;
			System.out.println(String.format("%s ModuleList: %s, %s", tabs, list.name(), list.comment()));
			printParameters(level, list.parameters());
			for (ModuleConfig child : list.children())
				printObject(level + 1, child);
		} else if (node instanceof ConfigList) {
			ConfigList list = (ConfigList) node;
			System.out.println(String.format("%s ConfigList: %s, %s", tabs, list.name(), list.comment()));
			printParameters(level, list.parameters());
			for (ConfigNode child : list.children())
				printObject(level + 1, child);
		} else if (node instanceof ModuleConfig) {
			ModuleConfig module = (ModuleConfig) node;
			System.out.println(String.format("%s ModuleConfig: %s, %s", tabs, module.name(), module.comment()));
			printParameters(level, module.parameters());
			for (ConfigNode child : module.children())
				printObject(level + 1, child);
		} else if (node instanceof ConfigNode) {
			System.out.println(String.format("%s ConfigNode: %s, %s", tabs, node.name(), node.comment()));
			printParameters(level, node.parameters());
		} else
			System.out.println(String.format("%s {??}", tabs));
	}

	public void printParameters(int level, HashMap<String, Object> params) {
		if (params == null)
			return;
		StringBuilder tabs = new StringBuilder();
		for (int i = 0; i < level; i++)
			tabs.append("\t");
		for (Entry<String, Object> entry : params.entrySet())
			System.out.println(String.format("%s Param %s: %s", tabs, entry.getKey(), entry.getValue().toString()));
	}

}
