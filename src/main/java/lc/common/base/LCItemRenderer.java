package lc.common.base;

import lc.api.defs.IDefinitionReference;
import lc.api.defs.ILanteaCraftRenderer;
import lc.common.impl.registry.DefinitionReference;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

/**
 * Generic item rendering stub.
 *
 * @author AfterLifeLochie
 *
 */
public abstract class LCItemRenderer implements ILanteaCraftRenderer {

	/**
	 * Get the parent renderer. Called when a render function cannot be
	 * completed by the current renderer.
	 *
	 * @return The parent renderer.
	 */
	public abstract LCItemRenderer getParent();

	/**
	 * Ask if this renderer should handle a particular render type.
	 *
	 * @param item
	 *            The item stack
	 * @param type
	 *            The render type
	 * @return If this renderer should handle the particular render type.
	 */
	public abstract boolean handleRenderType(ItemStack item, ItemRenderType type);

	/**
	 * Ask the renderer if it requires a render helper be used.
	 *
	 * @param type
	 *            The render type
	 * @param item
	 *            The item stack
	 * @param helper
	 *            The helper type
	 * @return If the helper should be applied
	 */
	public abstract boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper);

	/**
	 * Called to render the item
	 *
	 * @param type
	 *            The render type
	 * @param item
	 *            The item stack
	 * @param data
	 *            The parameters
	 * @return If the rendering was successful
	 */
	public abstract boolean renderItem(ItemRenderType type, ItemStack item, Object... data);

	@Override
	public IDefinitionReference ref() {
		return new DefinitionReference(this);
	}

}
