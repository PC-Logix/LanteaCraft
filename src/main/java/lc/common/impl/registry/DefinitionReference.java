package lc.common.impl.registry;

import lc.api.defs.IGameDef;

public class DefinitionReference {

	private final IGameDef def;
	private final Object[] params;

	public DefinitionReference(IGameDef def) {
		this.def = def;
		this.params = null;
	}

	public DefinitionReference(IGameDef def, Object... params) {
		this.def = def;
		this.params = params;
	}

	public IGameDef reference() {
		return def;
	}

	public Object[] parameters() {
		return params;
	}
}
