package pcl.lc.items;
import pcl.lc.LanteaCraft;
import pcl.lc.guis.GuiJacksonNotebook;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemJacksonNotebook extends Item {

	public ItemJacksonNotebook(int par1) {
		super(par1);
	}
	
	@Override
	public String getIconString() {
		return LanteaCraft.getAssetKey() + ":notebook";
	}

	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiJacksonNotebook());
		return par1ItemStack;
	}

	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiJacksonNotebook());
		return true;
	}

}
