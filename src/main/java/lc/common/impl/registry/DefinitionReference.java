package lc.common.impl.registry;

import java.util.ArrayList;

import lc.api.defs.IDefinitionReference;
import lc.api.defs.IGameDef;

/**
 * Definition reference implementation
 * 
 * @author AfterLifeLochie
 *
 */
public class DefinitionReference implements IDefinitionReference {

	private final IGameDef def;
	private final ArrayList<Object> params;

	/**
	 * Default constructor
	 * 
	 * @param def
	 *            The definition to reference
	 */
	public DefinitionReference(IGameDef def) {
		if (def == null)
			throw new RuntimeException("Cannot create a null definition reference.");
		this.def = def;
		params = new ArrayList<Object>();
	}

	/**
	 * Constructor with initial parameters
	 * 
	 * @param def
	 *            The defintion to reference
	 * @param params
	 *            The parameters to store
	 */
	public DefinitionReference(IGameDef def, Object... params) {
		this(def);
		for (Object o : params)
			this.params.add(o);
	}

	@Override
	public IGameDef reference() {
		return def;
	}

	@Override
	public Object[] parameters() {
		return params == null || params.size() == 0 ? null : params.toArray();
	}

	@Override
	public DefinitionReference push(int i, Object v) {
		params.set(i, v);
		return this;
	}

	@Override
	public DefinitionReference pushAll(Object... paramList) {
		params.clear();
		for (Object obj : paramList)
			params.add(obj);
		return this;
	}

	@Override
	public DefinitionReference copy() {
		if (params != null && params.size() > 0)
			return new DefinitionReference(def, parameters());
		return new DefinitionReference(def);
	}
}
