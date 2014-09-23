package lc.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.api.rendering.IBlockRenderInfo;
import lc.api.stargate.StargateType;
import lc.common.base.LCBlock;
import lc.core.ResourceAccess;
import lc.items.ItemBlockStargateBase;
import lc.tiles.TileStargateBase;

@Definition(name = "stargateBase", type = ComponentType.STARGATE, blockClass = BlockStargateBase.class, itemBlockClass = ItemBlockStargateBase.class, tileClass = TileStargateBase.class)
public class BlockStargateBase extends LCBlock {

	private static final int blockCount = StargateType.count();
	
	private static final IBlockRenderInfo renderInfo = new IBlockRenderInfo() {

		@Override
		public boolean doWorldRender(IBlockAccess access, int data, int x, int y, int z) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean doInventoryRender(int data) {
			// TODO Auto-generated method stub
			return true;
		}
		
	};

	protected IIcon topAndBottomTexture[] = new IIcon[StargateType.count()];
	protected IIcon frontTexture[] = new IIcon[StargateType.count()];
	protected IIcon sideTexture[] = new IIcon[StargateType.count()];

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
	public IBlockRenderInfo block() { return BlockStargateBase.renderInfo; }
}
