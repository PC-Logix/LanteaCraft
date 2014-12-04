package lc.common.base;

import lc.api.defs.ILanteaCraftRenderer;
import lc.common.LCLog;
import lc.common.impl.registry.DefinitionRegistry;
import lc.common.impl.registry.DefinitionRegistry.RendererType;
import lc.core.LCRuntime;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

/**
 * Item rendering hook.
 *
 * @author AfterLifeLochie
 *
 */
public class LCItemRenderHook implements IItemRenderer {

	private DefinitionRegistry registry;

	/**
	 * Create a new rendering hook.
	 */
	public LCItemRenderHook() {
		registry = (DefinitionRegistry) LCRuntime.runtime.registries().definitions();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		boolean flag = true;
		ILanteaCraftRenderer worker = registry.getRendererFor(RendererType.ITEM, item.getItem().getClass());
		if (worker == null && !(worker instanceof LCItemRenderer))
			flag = false;
		else {
			try {
				LCItemRenderer itemRenderer = (LCItemRenderer) worker;
				while (itemRenderer != null && !itemRenderer.handleRenderType(item, type)) {
					worker = itemRenderer.getParent();
					if (worker == null || !(worker instanceof LCTileRenderer)) {
						flag = false;
						break;
					}
				}
			} catch (Throwable t) {
				LCLog.warn("Uncaught item rendering exception.", t);
				flag = false;
			}
		}
		return flag;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		boolean flag = true;
		ILanteaCraftRenderer worker = registry.getRendererFor(RendererType.ITEM, item.getItem().getClass());
		if (worker == null && !(worker instanceof LCItemRenderer))
			flag = false;
		else {
			try {
				LCItemRenderer itemRenderer = (LCItemRenderer) worker;
				while (itemRenderer != null && !itemRenderer.shouldUseRenderHelper(type, item, helper)) {
					worker = itemRenderer.getParent();
					if (worker == null || !(worker instanceof LCTileRenderer)) {
						flag = false;
						break;
					}
				}
			} catch (Throwable t) {
				LCLog.warn("Uncaught item rendering exception.", t);
				flag = false;
			}
		}
		return flag;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		ILanteaCraftRenderer worker = registry.getRendererFor(RendererType.ITEM, item.getItem().getClass());
		if (worker != null && worker instanceof LCItemRenderer) {
			try {
				LCItemRenderer itemRenderer = (LCItemRenderer) worker;
				while (itemRenderer != null && !itemRenderer.renderItem(type, item, data)) {
					worker = itemRenderer.getParent();
					if (worker == null || !(worker instanceof LCTileRenderer))
						break;
				}
			} catch (Throwable t) {
				LCLog.warn("Uncaught item rendering exception.", t);
			}
		}
	}
}
