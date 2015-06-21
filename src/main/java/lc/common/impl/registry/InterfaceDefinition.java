package lc.common.impl.registry;

import lc.api.defs.IDefinitionReference;
import lc.api.defs.IInterfaceDefinition;

/**
 * Implementation of definitions for interface
 * 
 * @author AfterLifeLochie
 *
 */
public class InterfaceDefinition implements IInterfaceDefinition {

	private String name;
	private String containerClass;
	private String guiClass;

	private int regId = -1;

	/**
	 * Create an interface definition
	 * 
	 * @param name
	 *            The name of the definition
	 * @param containerClass
	 *            The name of the container class
	 * @param guiClass
	 *            The name of the GUI class
	 */
	public InterfaceDefinition(String name, String containerClass, String guiClass) {
		this.name = name;
		this.containerClass = containerClass;
		this.guiClass = guiClass;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getContainerClass() {
		return containerClass;
	}

	@Override
	public String getGUIClass() {
		return guiClass;
	}

	@Override
	public int getGUIID() {
		return regId;
	}

	@Override
	public IDefinitionReference ref() {
		return new DefinitionReference(this);
	}

	public void setGUIID(int regId) {
		this.regId = regId;
	}
}
