package lc.common.base;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import lc.api.defs.IDefinitionReference;
import lc.api.defs.ILanteaCraftRenderer;
import lc.common.impl.registry.DefinitionReference;

public abstract class LCItemRenderer implements ILanteaCraftRenderer {

	public abstract LCItemRenderer getParent();

	public abstract boolean handleRenderType(ItemStack item, ItemRenderType type);

	public abstract boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper);

	public abstract boolean renderItem(ItemRenderType type, ItemStack item, Object... data);

	@Override
	public IDefinitionReference ref() {
		return new DefinitionReference(this);
	}

}
