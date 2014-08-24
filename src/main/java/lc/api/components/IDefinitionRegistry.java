package lc.api.components;

import lc.api.defs.ILanteaCraftDefinition;

public interface IDefinitionRegistry {

	public void addDefinition(ILanteaCraftDefinition definition);

	public ILanteaCraftDefinition getDefinition(String name);

}
