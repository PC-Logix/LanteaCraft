package pcl.lc.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import pcl.common.helpers.ScanningHelper;
import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityRingPlatform;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTransportRingActivator extends Item {

	public ItemTransportRingActivator(int id) {
		super(id);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return LanteaCraft.getAssetKey() + ":transport_ring_activator_" + LanteaCraft.getProxy().getRenderMode();
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4,
			int par5, int par6, int par7, float par8, float par9, float par10) {
		onItemClicked(par1ItemStack, par3World, par2EntityPlayer);
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		onItemClicked(par1ItemStack, par2World, par3EntityPlayer);
		return par1ItemStack;
	}

	private void onItemClicked(ItemStack stackOf, World world, EntityPlayer player) {
		if (!world.isRemote) {
			TileEntity of = ScanningHelper.findNearestTileEntityOf(world, TileEntityRingPlatform.class,
					(int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ),
					AxisAlignedBB.getBoundingBox(-5, -5, -5, 5, 5, 5));
			if (of != null && (of instanceof TileEntityRingPlatform)) {
				TileEntityRingPlatform platform = (TileEntityRingPlatform) of;
				platform.connect();
			}
		}
	}

}
