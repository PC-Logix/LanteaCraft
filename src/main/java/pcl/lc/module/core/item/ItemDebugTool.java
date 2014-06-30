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
import pcl.lc.module.power.tile.TileEntityNaquadahGenerator;
import pcl.lc.module.stargate.tile.TileEntityStargateBase;
import pcl.lc.module.stargate.tile.TileEntityStargateController;
import pcl.lc.module.stargate.tile.TileEntityStargateRing;
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

		if (par3World.isRemote) {
			AudioEngine engine = LanteaCraft.getProxy().getAudioEngine();
			AudioSource source = engine.create(this, new AudioPosition(par3World, new Vector3(par4, par5, par6)),
					"weapon/goauld_staff_shot.ogg", false, false, 1.0F);
			source.activate();
			source.play();
		}

		TileEntity entity = par3World.getTileEntity(par4, par5, par6);
		if (entity instanceof TileEntityStargateBase) {
			TileEntityStargateBase base = (TileEntityStargateBase) entity;
			par2EntityPlayer.addChatMessage(new ChatComponentText("type: TileEntityStargateBase"));
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
		} else if (entity instanceof TileEntityStargateRing) {
			TileEntityStargateRing ring = (TileEntityStargateRing) entity;
			par2EntityPlayer.addChatMessage(new ChatComponentText("type: TileEntityStargateBase"));
			par2EntityPlayer.addChatMessage(new ChatComponentText("isMerged: "
					+ (ring.getAsPart().isMerged() ? "yes" : "no")));
		} else if (entity instanceof TileEntityStargateController) {
			TileEntityStargateController controller = (TileEntityStargateController) entity;
			par2EntityPlayer.addChatMessage(new ChatComponentText("type: TileEntityStargateController"));
			par2EntityPlayer.addChatMessage(new ChatComponentText("isLinkedToBase: "
					+ (controller.isLinkedToStargate ? "yes" : "no")));
		}

		if (entity instanceof TileEntityNaquadahGenerator) {
			TileEntityNaquadahGenerator generator = (TileEntityNaquadahGenerator) entity;
			par2EntityPlayer.addChatMessage(new ChatComponentText("type: TileEntityNaquadahGenerator"));
			par2EntityPlayer.addChatMessage(new ChatComponentText("simulating: " + generator.simulate));
			par2EntityPlayer.addChatMessage(new ChatComponentText("energy: " + generator.energy));
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
