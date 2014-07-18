package pcl.lc.module.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.lc.core.ResourceAccess;

public class ItemJacksonNotebook extends Item {

	public ItemJacksonNotebook() {
		super();
	}

	@Override
	public String getIconString() {
		return ResourceAccess.formatResourceName("${ASSET_KEY}:notebook_${TEX_QUALITY}");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		Minecraft.getMinecraft().displayGuiScreen(new pcl.lc.module.core.gui.GuiJacksonNotebook());
		return par1ItemStack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ) {
		TileEntity theTile = world.getTileEntity(x, y, z);
		if (theTile != null)
			Minecraft.getMinecraft().displayGuiScreen(
					new pcl.lc.module.core.gui.GuiJacksonNotebook(theTile.getClass().getName()));
		else if (world.getBlock(x, y, z) != null)
			Minecraft.getMinecraft().displayGuiScreen(
					new pcl.lc.module.core.gui.GuiJacksonNotebook(world.getBlock(x, y, z).getClass().getName()));
		return true;
	}

}
