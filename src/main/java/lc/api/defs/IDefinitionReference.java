package lc.api.defs;

public interface IDefinitionReference {

	public IGameDef reference();

	public Object[] parameters();

	public IDefinitionReference push(int i, Object v);

	public IDefinitionReference pushAll(Object... paramList);

	public IDefinitionReference copy();

}
