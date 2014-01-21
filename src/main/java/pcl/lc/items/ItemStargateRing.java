package pcl.lc.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import pcl.lc.LanteaCraft;
import pcl.lc.LanteaCraft.Blocks;

public class ItemStargateRing extends ItemBlock {

	public ItemStargateRing(int id) {
		super(id);
		setHasSubtypes(true);
	}

	@Override
	public Icon getIconFromDamage(int i) {
		return Blocks.stargateRingBlock.getIcon(0, i);
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
		return LanteaCraft.getAssetKey() + ":" + getUnlocalizedName() + "_"
				+ LanteaCraft.getProxy().getRenderMode();
	}

	public static String subItemName(int i) {
		return "tile.stargateRing." + i;
	}

}
