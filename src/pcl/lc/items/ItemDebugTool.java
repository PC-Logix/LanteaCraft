package pcl.lc.items;

import java.util.List;
import java.util.logging.Level;

import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.lc.tileentity.TileEntityStargateController;
import pcl.lc.tileentity.TileEntityStargateRing;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDebugTool extends Item {
	@SideOnly(Side.CLIENT)
	private Icon theIcon;

	public ItemDebugTool(int itemid) {
		super(itemid);
	}

	/**
	 * Callback for item usage. If the item does something special on right
	 * clicking, he will have one of those. Return True if something happen and
	 * false if it don't. This is for ITEMS, not BLOCKS
	 */
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4,
			int par5, int par6, int par7, float par8, float par9, float par10) {
		String side = (par3World.isRemote) ? "client" : "server";
		int i1 = par3World.getBlockId(par4, par5, par6);
		LanteaCraft.getLogger().log(Level.INFO, "Debugger used at (" + par4 + ", " + par5 + ", " + par6 + ") on " + side);
		par2EntityPlayer.addChatMessage("Data for (" + par4 + ", " + par5 + ", " + par6 + ") side " + side + ":");

		TileEntity entity = par3World.getBlockTileEntity(par4, par5, par6);
		if (entity instanceof TileEntityStargateBase) {
			TileEntityStargateBase base = (TileEntityStargateBase) entity;
			par2EntityPlayer.addChatMessage("type: TileEntityStargateBase");
			par2EntityPlayer.addChatMessage("isValid: " + (base.getAsStructure().isValid() ? "yes" : "no"));
			par2EntityPlayer.addChatMessage("partCount: " + base.getAsStructure().getPartCount());
		} else if (entity instanceof TileEntityStargateRing) {
			TileEntityStargateRing ring = (TileEntityStargateRing) entity;
			par2EntityPlayer.addChatMessage("type: TileEntityStargateBase");
			par2EntityPlayer.addChatMessage("isMerged: " + (ring.getAsPart().isMerged() ? "yes" : "no"));
		} else if (entity instanceof TileEntityStargateController) {
			TileEntityStargateController controller = (TileEntityStargateController) entity;
			par2EntityPlayer.addChatMessage("type: TileEntityStargateController");
			par2EntityPlayer.addChatMessage("isLinkedToBase: " + (controller.isLinkedToStargate ? "yes" : "no"));
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon("gcewing_sg:debugger");
	}

}
