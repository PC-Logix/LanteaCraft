package pcl.lc.module.stargate.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import pcl.lc.LanteaCraft;
import pcl.lc.module.ModuleStargates;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTransporterRing extends ItemBlock {

	public ItemTransporterRing(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public IIcon getIconFromDamage(int i) {
		return ModuleStargates.Blocks.transporterRing.getIcon(0, i);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return subItemName(stack.getItemDamage());
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return LanteaCraft.getAssetKey() + ":" + getUnlocalizedName() + "_" + LanteaCraft.getProxy().getRenderMode();
	}

	public static String subItemName(int i) {
		return "tile.transporterRing." + i;
	}

}
