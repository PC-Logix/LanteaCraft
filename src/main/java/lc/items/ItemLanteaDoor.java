package lc.items;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import lc.ResourceAccess;
import lc.common.LCLog;
import lc.common.base.LCItemBlock;

public class ItemLanteaDoor extends LCItemBlock {

	private IIcon doorIcon;

	public ItemLanteaDoor(Block block) {
		super(block);
	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		return doorIcon;
	}

	@Override
	public void registerIcons(IIconRegister register) {
		doorIcon = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
				"lantean_door"));
	}

}
