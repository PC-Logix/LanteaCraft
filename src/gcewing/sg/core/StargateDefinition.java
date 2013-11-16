package gcewing.sg.core;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import gcewing.sg.SGCraft;
import gcewing.sg.config.LanguageHelper;
import gcewing.sg.multiblock.IStructureConfiguration;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class StargateDefinition {

	private EnumStargateType typeof;
	private IStructureConfiguration struct;

	private Block stargateBaseBlock;
	private Block stargateRingBlock;
	private Block stargateChevronBlock;

	public StargateDefinition(EnumStargateType typeof, IStructureConfiguration structHost) {
		this.typeof = typeof;
		this.struct = structHost;
	}

	public void buildDefinition() {

	}

	private void buildBlockDefinition(Class<? extends Block> blockClazz, Class<? extends ItemBlock> itemClazz, int id, String partName) {
		try {
			Constructor<? extends Block> ctor = blockClazz.getConstructor(int.class);
			Block block = ctor.newInstance(id);

			String unlocalizedName = LanguageHelper.getUnlocNameForGatePart(typeof, partName);
			block.setUnlocalizedName(SGCraft.getInstance().getAssetKey() + ":" + unlocalizedName);

			block.setTextureName(SGCraft.getInstance().getAssetKey() + ":" + unlocalizedName + "_"
					+ SGCraft.getProxy().getRenderMode());
			
			block.setCreativeTab(SGCraft.getInstance().getCreativeTab());
			GameRegistry.registerBlock(block, itemClazz, idForName);
			LanguageRegistry.addName(block, localizedName);
			return block;
		} catch (Exception e) {
			SGCraft.getLogger().log(Level.SEVERE, "Failed to register block, an exception occured.", e);
			throw new RuntimeException(e);
		}
	}

	public EnumStargateType getTypeof() {
		return typeof;
	}

	public Block getBaseBlock() {
		return stargateBaseBlock;
	}

	public Block getRingBlock() {
		return stargateRingBlock;
	}

	public Block getChevronBlock() {
		return stargateChevronBlock;
	}

}
