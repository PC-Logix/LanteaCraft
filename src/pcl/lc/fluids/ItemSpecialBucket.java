package pcl.lc.fluids;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.lc.LanteaCraft;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;

public class ItemSpecialBucket extends ItemBucket {
	private String iconName;

	public ItemSpecialBucket(int i, Block hostBlock) {
		super(i, hostBlock.blockID);
		LanteaCraft.getSpecialBucketHandler().registerBucketMapping(hostBlock, this);
		setCreativeTab(LanteaCraft.getCreativeTab());
		setContainerItem(Item.bucketEmpty);
	}

	@Override
	public Item setUnlocalizedName(String par1Str) {
		iconName = par1Str;
		return super.setUnlocalizedName(par1Str);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(LanteaCraft.getAssetKey() + ":" + iconName);
	}
}
