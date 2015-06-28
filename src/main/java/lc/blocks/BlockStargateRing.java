package lc.blocks;

import java.util.List;

import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.api.rendering.IBlockRenderInfo;
import lc.api.stargate.StargateType;
import lc.common.base.LCBlock;
import lc.common.base.LCTile;
import lc.common.base.multiblock.MultiblockState;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;
import lc.items.ItemBlockStargateRing;
import lc.tiles.TileStargateRing;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

/**
 * Stargate ring implementation.
 *
 * @author AfterLifeLochie
 *
 */
@Definition(name = "stargateRing", type = ComponentType.STARGATE, blockClass = BlockStargateRing.class, itemBlockClass = ItemBlockStargateRing.class, tileClass = TileStargateRing.class)
public class BlockStargateRing extends LCBlock {

	/** Mask for all block types */
	private static final int blockMask = 2;
	/** Number of total blocks inside block */
	private static final int blockCount = StargateType.count() * blockMask;

	/** Mask for crafting blocks */
	private static final int blockCraftingMask = 1;
	/** Number of total craftable types */
	private static final int blockCraftingCount = blockCount;

	/** Top and bottom textures */
	IIcon topAndBottomTexture[] = new IIcon[StargateType.count()];
	/** Side textures */
	IIcon sideTextures[][] = new IIcon[StargateType.count()][blockMask];

	private static IBlockRenderInfo renderInfo = new IBlockRenderInfo() {
		@Override
		public boolean doWorldRender(IBlockAccess access, int data, int x, int y, int z) {
			return true;
		}

		@Override
		public boolean doProperty(String property, IBlockAccess access, int data, int x, int y, int z, boolean def) {
			if (property.equalsIgnoreCase("noRender")) {
				LCTile t = (LCTile) access.getTileEntity(x, y, z);
				if (t != null && t instanceof TileStargateRing)
					return ((TileStargateRing) t).getState() != MultiblockState.FORMED;
			}
			return def;
		}

		@Override
		public boolean doInventoryRender(int data) {
			return true;
		}
	};

	/** Default constructor */
	public BlockStargateRing() {
		super(Material.ground);
		setHardness(3F).setResistance(2000F);
		setOpaque(false).setProvidesInventory(false).setProvidesTypes(true);
	}

	@Override
	protected String getTextureName() {
		return ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}", getUnlocalizedName());
	}

	@Override
	public IIcon getIcon(int side, int data) {
		int typeof = getBaseType(data);
		if (side <= 1)
			return topAndBottomTexture[typeof];
		else
			return sideTextures[typeof][data % blockMask];
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		StargateType[] types = StargateType.values();
		for (StargateType typeof : types) {
			StringBuilder typename = new StringBuilder();
			typename.append("stargate_%s");
			if (typeof.getSuffix() != null && typeof.getSuffix().length() > 0)
				typename.append("_").append(typeof.getSuffix());
			topAndBottomTexture[typeof.ordinal()] = register.registerIcon(ResourceAccess.formatResourceName(
					"${ASSET_KEY}:%s_${TEX_QUALITY}", String.format(typename.toString(), "block")));
			sideTextures[typeof.ordinal()][0] = register.registerIcon(ResourceAccess.formatResourceName(
					"${ASSET_KEY}:%s_${TEX_QUALITY}", String.format(typename.toString(), "ring")));
			sideTextures[typeof.ordinal()][1] = register.registerIcon(ResourceAccess.formatResourceName(
					"${ASSET_KEY}:%s_${TEX_QUALITY}", String.format(typename.toString(), "chevron")));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < blockCraftingCount; i += blockCraftingMask)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public int damageDropped(int damage) {
		return damage;
	}

	/**
	 * Get the base type of this Stargate ring block
	 *
	 * @param metadata
	 *            The block metadata
	 * @return The base type
	 */
	public int getBaseType(int metadata) {
		return (int) Math.floor(metadata / blockMask);
	}

	@Override
	public IBlockRenderInfo renderInfoBlock() {
		return BlockStargateRing.renderInfo;
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub
		
	}

}
