package pcl.lc.module.stargate.render;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class HeldItemRenderer implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return (type == ItemRenderType.EQUIPPED_FIRST_PERSON);
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		// There's no way to get partialticks from here. :<
	}

}
