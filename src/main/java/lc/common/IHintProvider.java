package lc.common;

import lc.api.defs.ILanteaCraftDefinition;
import lc.api.defs.IRecipeDefinition;

public interface IHintProvider {

	public void preInit();

	public void init();

	public void postInit();

	public void provideHints(ILanteaCraftDefinition definition);

	public void provideHints(IRecipeDefinition definition);
}
