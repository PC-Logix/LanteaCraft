package lc.items;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import lc.common.base.LCItemBlock;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;

/**
 * Lantean door item block implementation
 * 
 * @author AfterLifeLochie
 *
 */
public class ItemLanteaDoor extends LCItemBlock {

	private IIcon doorIcon[] = new IIcon[2];

	public ItemLanteaDoor(Block block) {
		super(block);
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		return doorIcon[damage];
	}

	@Override
	public void registerIcons(IIconRegister register) {
		doorIcon[0] = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
				"lantean_door"));
		doorIcon[1] = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
				"goauld_door"));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return subItemName(stack.getItemDamage());
	}

	private static String subItemName(int i) {
		return "tile.lanteaDoor." + i;
	}

}
