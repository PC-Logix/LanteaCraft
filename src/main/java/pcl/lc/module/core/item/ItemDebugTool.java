package pcl.lc.module.core.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;

import pcl.common.util.Vector3;
import pcl.lc.LanteaCraft;
import pcl.lc.client.audio.AudioEngine;
import pcl.lc.client.audio.AudioPosition;
import pcl.lc.client.audio.AudioSource;
import pcl.lc.module.power.tile.TileNaquadahGenerator;
import pcl.lc.module.stargate.tile.TileStargateBase;
import pcl.lc.module.stargate.tile.TileStargateDHD;
import pcl.lc.module.stargate.tile.TileStargateRing;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDebugTool extends Item {
	@SideOnly(Side.CLIENT)
	private IIcon theIcon;

	public ItemDebugTool() {
		super();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return LanteaCraft.getAssetKey() + ":" + getUnlocalizedName() + "_" + LanteaCraft.getProxy().getRenderMode();
	}

	/**
	 * Callback for item usage. If the item does something special on right
	 * clicking, he will have one of those. Return True if something happen and
	 * false if it don't. This is for ITEMS, not BLOCKS
	 */
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4,
			int par5, int par6, int par7, float par8, float par9, float par10) {

		String side = (par3World.isRemote) ? "client" : "server";
		LanteaCraft.getLogger().log(Level.INFO,
				"Debugger used at (" + par4 + ", " + par5 + ", " + par6 + ") on " + side);
		par2EntityPlayer.addChatMessage(new ChatComponentText("Data for (" + par4 + ", " + par5 + ", " + par6
				+ ") side " + side + ":"));

		TileEntity entity = par3World.getTileEntity(par4, par5, par6);
		if (entity instanceof TileStargateBase) {
			TileStargateBase base = (TileStargateBase) entity;
			par2EntityPlayer.addChatMessage(new ChatComponentText("type: TileStargateBase"));
			par2EntityPlayer.addChatMessage(new ChatComponentText("mValid: "
					+ (base.getAsStructure().isValid() ? "yes" : "no")));
			par2EntityPlayer.addChatMessage(new ChatComponentText("mRotation: "
					+ base.getAsStructure().getOrientation()));
			par2EntityPlayer
					.addChatMessage(new ChatComponentText("mPartCount: " + base.getAsStructure().getPartCount()));
			par2EntityPlayer.addChatMessage(new ChatComponentText("wValid: "
					+ ((base.getAsStructure().isValidStructure(par3World, par4, par5, par6)) ? "yes" : "no")));
			if (base.getState() != null)
				par2EntityPlayer.addChatMessage(new ChatComponentText("mState: " + base.getState()));
			par2EntityPlayer.addChatMessage(new ChatComponentText("ticks: " + base.getTicks()));
		} else if (entity instanceof TileStargateRing) {
			TileStargateRing ring = (TileStargateRing) entity;
			par2EntityPlayer.addChatMessage(new ChatComponentText("type: TileStargateBase"));
			par2EntityPlayer.addChatMessage(new ChatComponentText("isMerged: "
					+ (ring.getAsPart().isMerged() ? "yes" : "no")));
		} else if (entity instanceof TileStargateDHD) {
			TileStargateDHD controller = (TileStargateDHD) entity;
			par2EntityPlayer.addChatMessage(new ChatComponentText("type: TileStargateDHD"));
			par2EntityPlayer.addChatMessage(new ChatComponentText("isLinkedToBase: "
					+ (controller.isLinkedToStargate ? "yes" : "no")));
		}

		if (entity instanceof TileNaquadahGenerator) {
			TileNaquadahGenerator generator = (TileNaquadahGenerator) entity;
			par2EntityPlayer.addChatMessage(new ChatComponentText("type: TileNaquadahGenerator"));
			par2EntityPlayer.addChatMessage(new ChatComponentText("simulating: " + (Boolean) generator.metadata.get("simulating")));
			par2EntityPlayer.addChatMessage(new ChatComponentText("energy: " + (Double) generator.metadata.get("energy")));
			par2EntityPlayer.addChatMessage(new ChatComponentText("exportEnergy: "
					+ generator.getAvailableExportEnergy()));
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		itemIcon = par1IconRegister.registerIcon("pcl_lc:creative_icon");
	}

}
