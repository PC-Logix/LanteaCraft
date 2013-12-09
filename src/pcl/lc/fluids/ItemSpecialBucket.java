package pcl.lc.fluids;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.common.helpers.SpecialBucketHandler;
import pcl.lc.LanteaCraft;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;

/**
 * ItemSpecialBucket is a special bucket implementation which allows Fluids to create custom
 * bucket types generically.
 * 
 * @author AfterLifeLochie
 */
public class ItemSpecialBucket extends ItemBucket {

	/**
	 * Icon name associated with the unlocalized name of this bucket instance.
	 */
	private String iconName;

	/**
	 * Creates an instance of an ItemSpecialBucket and initializes it with default properties
	 * and with the {@link SpecialBucketHandler} registry.
	 * 
	 * @param i
	 *            The item ID to use for this bucket.
	 * @param hostBlock
	 *            The fluid block this Bucket is hosting.
	 */
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
