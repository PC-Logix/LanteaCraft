package pcl.lc.module.stargate.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import pcl.lc.LanteaCraft;
import pcl.lc.core.ResourceAccess;
import pcl.lc.module.stargate.TransporterRingMultiblock;
import pcl.lc.module.stargate.tile.TileTransporterRing;
import pcl.lc.util.ScanningHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTransportRingActivator extends Item {

	public ItemTransportRingActivator() {
		super();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}", "transport_ring_activator");
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
			TileEntity of = ScanningHelper.findNearestTileEntityOf(world, TileTransporterRing.class,
					(int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ),
					AxisAlignedBB.getBoundingBox(-5, -5, -5, 5, 5, 5));
			if (of != null && (of instanceof TileTransporterRing)) {
				TileTransporterRing platform = (TileTransporterRing) of;
				if (platform.isHost())
					if (!platform.getAsStructure().isBusy())
						platform.getAsStructure().connect();
					else {
						TransporterRingMultiblock mblock = (TransporterRingMultiblock) platform.getAsPart()
								.findHostMultiblock(false);
						if (!mblock.isBusy())
							mblock.connect();
					}
			}
		}
	}

}
