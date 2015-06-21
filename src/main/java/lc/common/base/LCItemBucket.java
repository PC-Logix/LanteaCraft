package lc.common.base;

import lc.common.configuration.IConfigure;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.util.LCCreativeTabManager;
import lc.common.util.SpecialBucketHandler;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * LCItemBucket is a special bucket implementation which allows Fluids to create
 * custom bucket types generically.
 *
 * @author AfterLifeLochie
 */
public class LCItemBucket extends ItemBucket implements IConfigure {

	/**
	 * Creates an instance of an LCItemBucket and initializes it with default
	 * properties and with the {@link SpecialBucketHandler} registry.
	 *
	 * @param hostBlock
	 *            The fluid block this Bucket is hosting.
	 */
	public LCItemBucket(LCBlock hostBlock) {
		super(hostBlock);
		SpecialBucketHandler.registerBucketMapping(hostBlock, this);
		setCreativeTab(LCCreativeTabManager.getTab("LanteaCraft"));
		setContainerItem(Items.bucket);
	}

	@Override
	public Item setUnlocalizedName(String par1Str) {
		return super.setUnlocalizedName(par1Str);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		// TODO: resourceaccess qualifier
		// itemIcon =
		// par1IconRegister.registerIcon(ResourceAccess.formatResourceName(
		// "${ASSET_KEY}:bucket_%s_${TEX_QUALITY}", iconName));
	}

	/**
	 * Set the target texture of the bucket.
	 *
	 * @param bucketTextureName
	 *            The texture name for the bucket.
	 */
	public void setTargetTexture(String bucketTextureName) {
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}
}
