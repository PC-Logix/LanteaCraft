package pcl.lc.items;

import java.util.logging.Level;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import pcl.common.audio.AudioEngine;
import pcl.common.audio.AudioPosition;
import pcl.common.audio.AudioSource;
import pcl.common.util.Vector3;
import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityNaquadahGenerator;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.lc.tileentity.TileEntityStargateController;
import pcl.lc.tileentity.TileEntityStargateRing;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDebugTool extends Item {
	@SideOnly(Side.CLIENT)
	private Icon theIcon;

	public ItemDebugTool(int itemid) {
		super(itemid);
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
		int i1 = par3World.getBlockId(par4, par5, par6);
		LanteaCraft.getLogger().log(Level.INFO,
				"Debugger used at (" + par4 + ", " + par5 + ", " + par6 + ") on " + side);
		par2EntityPlayer.addChatMessage("Data for (" + par4 + ", " + par5 + ", " + par6 + ") side " + side + ":");

		if (par3World.isRemote) {
			AudioEngine engine = LanteaCraft.getProxy().getAudioEngine();
			AudioSource source = engine.create(this, new AudioPosition(par3World, new Vector3(par4, par5, par6)),
					"weapon/goauld_staff_shot.ogg", false, false, 1.0F);
			source.activate();
			source.play();
		}

		TileEntity entity = par3World.getBlockTileEntity(par4, par5, par6);
		if (entity instanceof TileEntityStargateBase) {
			TileEntityStargateBase base = (TileEntityStargateBase) entity;
			par2EntityPlayer.addChatMessage("type: TileEntityStargateBase");
			par2EntityPlayer.addChatMessage("mValid: " + (base.getAsStructure().isValid() ? "yes" : "no"));
			par2EntityPlayer.addChatMessage("mRotation: " + base.getAsStructure().getOrientation());
			par2EntityPlayer.addChatMessage("mPartCount: " + base.getAsStructure().getPartCount());
			par2EntityPlayer.addChatMessage("wValid: "
					+ ((base.getAsStructure().isValidStructure(par3World, par4, par5, par6)) ? "yes" : "no"));
			if (base.getState() != null)
				par2EntityPlayer.addChatMessage("mState: " + base.getState());
		} else if (entity instanceof TileEntityStargateRing) {
			TileEntityStargateRing ring = (TileEntityStargateRing) entity;
			par2EntityPlayer.addChatMessage("type: TileEntityStargateBase");
			par2EntityPlayer.addChatMessage("isMerged: " + (ring.getAsPart().isMerged() ? "yes" : "no"));
		} else if (entity instanceof TileEntityStargateController) {
			TileEntityStargateController controller = (TileEntityStargateController) entity;
			par2EntityPlayer.addChatMessage("type: TileEntityStargateController");
			par2EntityPlayer.addChatMessage("isLinkedToBase: " + (controller.isLinkedToStargate ? "yes" : "no"));
		}

		if (entity instanceof TileEntityNaquadahGenerator) {
			TileEntityNaquadahGenerator generator = (TileEntityNaquadahGenerator) entity;
			par2EntityPlayer.addChatMessage("type: TileEntityNaquadahGenerator");
			par2EntityPlayer.addChatMessage("simulating: " + generator.simulate);
			par2EntityPlayer.addChatMessage("energy: " + generator.energy);
			par2EntityPlayer.addChatMessage("exportEnergy: " + generator.getAvailableExportEnergy());
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		itemIcon = par1IconRegister.registerIcon("pcl_lc:creative_icon");
	}

}
