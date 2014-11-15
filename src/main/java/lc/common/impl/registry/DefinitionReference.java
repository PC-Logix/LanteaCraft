package lc.common.impl.registry;

import java.util.ArrayList;

import lc.api.defs.IGameDef;

public class DefinitionReference {

	private final IGameDef def;
	private final ArrayList<Object> params;

	public DefinitionReference(IGameDef def) {
		if (def == null)
			throw new RuntimeException("Cannot create a null definition reference.");
		this.def = def;
		this.params = new ArrayList<Object>();
	}

	public DefinitionReference(IGameDef def, Object... params) {
		this(def);
		for (Object o : params)
			this.params.add(o);
	}

	public IGameDef reference() {
		return def;
	}

	public Object[] parameters() {
		return (params == null || params.size() == 0) ? null : params.toArray();
	}

	public DefinitionReference setParameter(int i, Object v) {
		this.params.set(i, v);
		return this;
	}

	public DefinitionReference copy() {
		return new DefinitionReference(def, (ArrayList<Object>) params.clone());
	}
}
