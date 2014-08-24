package lc.api;

import lc.api.components.IRegistryContainer;
import lc.api.defs.Blocks;
import lc.api.defs.Items;

public interface ILCAPIProxy {

	public Blocks blocks();

	public Items items();

	public IRegistryContainer registries();

}
