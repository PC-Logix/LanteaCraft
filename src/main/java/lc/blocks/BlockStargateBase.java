package lc.blocks;

import java.util.List;

import lc.LCRuntime;
import lc.LanteaCraft;
import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.api.rendering.IBlockRenderInfo;
import lc.api.stargate.StargateType;
import lc.common.base.LCBlock;
import lc.common.base.LCTile;
import lc.common.base.multiblock.MultiblockState;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;
import lc.items.ItemBlockStargateBase;
import lc.tiles.TileStargateBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Stargate base implementation.
 *
 * @author AfterLifeLochie
 *
 */
@Definition(name = "stargateBase", type = ComponentType.STARGATE, blockClass = BlockStargateBase.class, itemBlockClass = ItemBlockStargateBase.class, tileClass = TileStargateBase.class)
public class BlockStargateBase extends LCBlock {

	private static final int blockCount = StargateType.count();

	private static IBlockRenderInfo renderInfo = new IBlockRenderInfo() {
		@Override
		public boolean doWorldRender(IBlockAccess access, int data, int x, int y, int z) {
			return true;
		}

		@Override
		public boolean doProperty(String property, IBlockAccess access, int data, int x, int y, int z, boolean def) {
			if (property.equalsIgnoreCase("noRender")) {
				LCTile t = (LCTile) access.getTileEntity(x, y, z);
				if (t != null && t instanceof TileStargateBase)
					return ((TileStargateBase) t).getState() != MultiblockState.FORMED;
			}
			return def;
		}

		@Override
		public boolean doInventoryRender(int data) {
			return true;
		}
	};

	/** Top and bottom texture map */
	protected IIcon topAndBottomTexture[] = new IIcon[StargateType.count()];
	/** Front texture map */
	protected IIcon frontTexture[] = new IIcon[StargateType.count()];
	/** Side texture map */
	protected IIcon sideTexture[] = new IIcon[StargateType.count()];

	/** Default constructor */
	public BlockStargateBase() {
		super(Material.ground);
		setHardness(3F).setResistance(2000F);
		setOpaque(false).setProvidesInventory(false).setProvidesTypes(true).setCanRotate(true);
	}

	@Override
	protected String getTextureName() {
		return ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}", getUnlocalizedName());
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
			frontTexture[typeof.ordinal()] = register.registerIcon(ResourceAccess.formatResourceName(
					"${ASSET_KEY}:%s_${TEX_QUALITY}", String.format(typename.toString(), "base_front")));
			sideTexture[typeof.ordinal()] = register.registerIcon(ResourceAccess.formatResourceName(
					"${ASSET_KEY}:%s_${TEX_QUALITY}", String.format(typename.toString(), "ring")));
		}
	}

	/**
	 * Get the type of this Stargate base block
	 *
	 * @param metadata
	 *            The metadata
	 * @return The base type
	 */
	public int getBaseType(int metadata) {
		return metadata;
	}

	@Override
	public IIcon getIcon(int side, int data) {
		int typeof = getBaseType(data);
		if (side <= 1)
			return topAndBottomTexture[typeof];
		else if (side == 3)
			return frontTexture[typeof];
		else
			return sideTexture[typeof];
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < blockCount; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public int damageDropped(int metadata) {
		return getBaseType(metadata);
	}

	@Override
	public IBlockRenderInfo renderInfoBlock() {
		return BlockStargateBase.renderInfo;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float cx,
			float cy, float cz) {
		TileStargateBase te = (TileStargateBase) world.getTileEntity(x, y, z);
		if (te != null && te.getState() == MultiblockState.FORMED) {
			player.openGui(LanteaCraft.instance, LCRuntime.runtime.interfaces().stargateUI.getGUIID(), world, x, y, z);
			return true;
		}
		return false;
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub
		
	}
}
