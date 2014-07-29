package pcl.lc.module.stargate.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pcl.lc.core.ResourceAccess;
import pcl.lc.module.ModuleStargates;
import pcl.lc.module.stargate.tile.TileStargateRing;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStargateRing extends ItemBlock {

	public ItemStargateRing(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public IIcon getIconFromDamage(int i) {
		return ModuleStargates.Blocks.stargateRingBlock.getIcon(0, i);
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
		return ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}", getUnlocalizedName());
	}

	public static String subItemName(int i) {
		return "tile.stargateRing." + i;
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ, int metadata) {
		boolean flag = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		if (!flag)
			return flag;
		TileStargateRing ring = (TileStargateRing) world.getTileEntity(x, y, z);
		ring.applyStackData(stack.stackTagCompound);
		return flag;
	}

}
