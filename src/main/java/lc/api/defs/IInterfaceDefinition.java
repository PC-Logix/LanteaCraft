package lc.api.defs;

public interface IInterfaceDefinition extends IGameDef {
	public String getName();

	public String getContainerClass();

	public String getGUIClass();

	public int getGUIID();

	public void setGUIID(int guiId);

}
