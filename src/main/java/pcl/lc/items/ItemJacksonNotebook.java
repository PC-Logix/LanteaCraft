package pcl.lc.items;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.lc.LanteaCraft;
import pcl.lc.guis.GuiJacksonNotebook;

public class ItemJacksonNotebook extends Item {

	public ItemJacksonNotebook(int par1) {
		super(par1);
	}

	@Override
	public String getIconString() {
		return LanteaCraft.getAssetKey() + ":notebook";
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiJacksonNotebook());
		return par1ItemStack;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ) {
		TileEntity theTile = world.getBlockTileEntity(x, y, z);
		if (theTile != null)
			Minecraft.getMinecraft().displayGuiScreen(new GuiJacksonNotebook(theTile.getClass().getName()));
		else {
			Block theBlock = null;
			if (world.getBlockId(x, y, z) != 0)
				theBlock = Block.blocksList[world.getBlockId(x, y, z)];
			if (theBlock != null)
				Minecraft.getMinecraft().displayGuiScreen(new GuiJacksonNotebook(theBlock.getClass().getName()));
		}
		return true;
	}

}
