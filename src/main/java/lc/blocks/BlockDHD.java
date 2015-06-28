package lc.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import lc.LCRuntime;
import lc.LanteaCraft;
import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.api.rendering.IBlockRenderInfo;
import lc.api.stargate.StargateType;
import lc.common.base.LCBlock;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;
import lc.items.ItemBlockDHD;
import lc.tiles.TileDHD;

/**
 * Stargate DHD block implementation
 * 
 * @author AfterLifeLochie
 *
 */
@Definition(name = "stargateDHD", type = ComponentType.STARGATE, blockClass = BlockDHD.class, itemBlockClass = ItemBlockDHD.class, tileClass = TileDHD.class)
public class BlockDHD extends LCBlock {

	/** Mask for all block types */
	private static final int blockMask = 1;
	/** Number of total blocks inside block */
	private static final int blockCount = StargateType.count() * blockMask;

	/** Mask for crafting blocks */
	private static final int blockCraftingMask = 1;
	/** Number of total craftable types */
	private static final int blockCraftingCount = blockCount;

	/** Rendering info */
	private static IBlockRenderInfo renderInfo = new IBlockRenderInfo() {
		@Override
		public boolean doWorldRender(IBlockAccess access, int data, int x, int y, int z) {
			return true;
		}

		@Override
		public boolean doProperty(String property, IBlockAccess access, int data, int x, int y, int z, boolean def) {
			if (property.equalsIgnoreCase("noRender")) {
				return false;
			}
			return def;
		}

		@Override
		public boolean doInventoryRender(int data) {
			return true;
		}
	};

	/** Top textures */
	IIcon topTexture[] = new IIcon[StargateType.count()];
	/** Bottom textures */
	IIcon bottomTexture[] = new IIcon[StargateType.count()];
	/** Side textures */
	IIcon sideTexture[] = new IIcon[StargateType.count()];

	/** Default constructor */
	public BlockDHD() {
		super(Material.ground);
		setHardness(3F).setResistance(2000F);
		setOpaque(false).setProvidesInventory(false).setProvidesTypes(true).setCanRotate(true);
	}

	@Override
	protected String getTextureName() {
		return ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}", getUnlocalizedName());
	}

	@Override
	public IIcon getIcon(int side, int data) {
		int typeof = getBaseType(data);
		if (side == 0)
			return bottomTexture[typeof];
		else if (side == 1)
			return topTexture[typeof];
		else
			return sideTexture[typeof];
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		StargateType[] types = StargateType.values();
		for (StargateType typeof : types) {
			StringBuilder typename = new StringBuilder();
			typename.append("controller_%s");
			if (typeof.getSuffix() != null && typeof.getSuffix().length() > 0)
				typename.append("_").append(typeof.getSuffix());
			topTexture[typeof.ordinal()] = register.registerIcon(ResourceAccess.formatResourceName(
					"${ASSET_KEY}:%s_${TEX_QUALITY}", String.format(typename.toString(), "top")));
			bottomTexture[typeof.ordinal()] = register.registerIcon(ResourceAccess.formatResourceName(
					"${ASSET_KEY}:%s_${TEX_QUALITY}", String.format(typename.toString(), "bottom")));
			sideTexture[typeof.ordinal()] = register.registerIcon(ResourceAccess.formatResourceName(
					"${ASSET_KEY}:%s_${TEX_QUALITY}", String.format(typename.toString(), "side")));
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

	@Override
	public IBlockRenderInfo renderInfoBlock() {
		return BlockDHD.renderInfo;
	}

	/**
	 * Get the base type of this DHD block
	 *
	 * @param metadata
	 *            The block metadata
	 * @return The base type
	 */
	public int getBaseType(int metadata) {
		return (int) Math.floor(metadata / blockMask);
	}

	/**
	 * Get the type of the DHD for the metadata specified
	 * 
	 * @param metadata
	 *            The metadata value
	 * @return The type of the DHD
	 */
	public StargateType getDHDType(int metadata) {
		return StargateType.fromOrdinal(getBaseType(metadata));
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float cx,
			float cy, float cz) {
		TileDHD te = (TileDHD) world.getTileEntity(x, y, z);
		if (te != null) {
			player.openGui(LanteaCraft.instance, LCRuntime.runtime.interfaces().dhdUI.getGUIID(), world, x, y, z);
			return true;
		}
		return false;
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}
}
