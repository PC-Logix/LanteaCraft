package pcl.lc.core;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

import pcl.lc.LanteaCraft;
import pcl.lc.blocks.BlockStargateBase;
import pcl.lc.blocks.BlockStargateController;
import pcl.lc.blocks.BlockStargateRing;
import pcl.lc.config.LanguageHelper;
import pcl.lc.items.ItemStargateRing;
import pcl.lc.multiblock.IStructureConfiguration;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

/**
 * Abstract definition of a gate type. Contains a reference to a specific gate type, and the
 * configuration which makes up the gate's structure. It is also responsible for initializing
 * the actual object instances and references to blocks and tile-entities.
 * 
 * @author AfterLifeLochie
 * 
 */
public class StargateDefinition {

	private EnumStargateType typeof;
	private IStructureConfiguration struct;

	private Block stargateBaseBlock;
	private Block stargateRingBlock;
	private Block stargateControllerBlock;

	private Class<?> typeRenderer;
	private Object renderer;

	/**
	 * Build a generic definition of a gate
	 * 
	 * @param typeof
	 *            The type of gate
	 * @param structHost
	 *            The structure configuration object
	 */
	public StargateDefinition(EnumStargateType typeof, Class<?> rendererClass, IStructureConfiguration structHost) {
		this.typeof = typeof;
		this.struct = structHost;
		this.typeRenderer = rendererClass;
	}

	/**
	 * Constructs the generic definition of the gate. This populates references to blocks and
	 * tile-entities, which also fires Forge registrations.
	 */
	public void buildDefinition() {
		// TODO: missing ref -> jp id (arg_2)
		stargateBaseBlock = buildBlockDefinition(BlockStargateBase.class, ItemBlock.class, 0, "base");
		// TODO: missing ref -> jp id (arg_2)
		stargateRingBlock = buildBlockDefinition(BlockStargateRing.class, ItemStargateRing.class, 0, "ring");
		// TODO: missing ref -> jp id (arg_2)
		stargateControllerBlock = buildBlockDefinition(BlockStargateController.class, ItemBlock.class, 0, "controller");
	}

	/**
	 * Builds an instance of a block for this particular abstract generic gate definition.
	 * 
	 * @param blockClazz
	 *            The class to use for the particular block instance
	 * @param itemClazz
	 *            The class to use when representing this block as an item
	 * @param id
	 *            The ID of this block
	 * @param partName
	 *            The name of the part this block represents
	 * @return The instance of the block for the current definition and the provided part type
	 */
	private Block buildBlockDefinition(Class<? extends Block> blockClazz, Class<? extends ItemBlock> itemClazz, int id,
			String partName) {
		try {
			// TODO: No such anonymous constructor exists yet
			Constructor<? extends Block> ctor = blockClazz.getConstructor(EnumStargateType.class, int.class);
			Block block = ctor.newInstance(typeof, id);

			String unlocalizedName = LanguageHelper.getUnlocNameForGatePart(typeof, partName);
			block.setUnlocalizedName(LanteaCraft.getInstance().getAssetKey() + ":" + unlocalizedName);

			block.setTextureName(LanteaCraft.getInstance().getAssetKey() + ":" + unlocalizedName + "_"
					+ LanteaCraft.getProxy().getRenderMode());

			String localizedName = LanguageHelper.getLocalNameForGatePart(typeof, partName);

			block.setCreativeTab(LanteaCraft.getInstance().getCreativeTab());
			GameRegistry.registerBlock(block, itemClazz, unlocalizedName);
			LanguageRegistry.addName(block, localizedName);
			return block;
		} catch (Exception e) {
			LanteaCraft.getLogger().log(Level.SEVERE, "Failed to register block, an exception occured.", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the type of this definition. Stargates use the EnumStargateType as a reference
	 * typeof, not directly as a class declaration.
	 * 
	 * @return The type of this definition
	 */
	public EnumStargateType getTypeof() {
		return typeof;
	}

	/**
	 * Gets the instance of the base block for this definition.
	 * 
	 * @return The instance of the base block for this definition
	 */
	public Block getBaseBlock() {
		return stargateBaseBlock;
	}

	/**
	 * Gets the instance of the ring block for this definition.
	 * 
	 * @return The instance of the ring block for this definition
	 */
	public Block getRingBlock() {
		return stargateRingBlock;
	}

	/**
	 * Gets the instance of the controller block for this definition. By default, this will be
	 * the standard controller, unless another type is explicitly declared in the definition.
	 * 
	 * @return The instance of the controller block for this definition
	 */
	public Block getControllerBlock() {
		return stargateControllerBlock;
	}

}
