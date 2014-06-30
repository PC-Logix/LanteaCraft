package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;

import pcl.common.helpers.RegistrationHelper;
import pcl.lc.api.internal.IModule;
import pcl.lc.blocks.BlockLanteaDecor;
import pcl.lc.blocks.BlockLanteaDecorGlass;
import pcl.lc.blocks.BlockLanteaDecorStair;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.items.ItemLanteaDecor;
import pcl.lc.items.ItemLanteaDecorGlass;
import pcl.lc.render.tileentity.TileEntityLanteaDecorGlassRenderer;
import pcl.lc.tileentity.TileEntityLanteaDecorGlass;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class ModuleDecor implements IModule {

	/**
	 * Container of all decoration materials
	 * 
	 * @author AfterLifeLochie
	 */
	public static enum EnumDecorMaterials {
		LANTEAN_STEEL("lantean"), LANTEAN_PATTERN_STEEL("lantean_pattern"), LANTEAN_GLASS("lantean_glass"), GOAULD_GOLD(
				"goauld"), GOAULD_PATTERN_GOLD("goauld_pattern");

		public static EnumDecorMaterials fromOrdinal(int ordinal) {
			return EnumDecorMaterials.values()[ordinal];
		}

		private final String label;
		private String unlocalizedName = null;

		EnumDecorMaterials(String label) {
			this.label = label;
		}

		/**
		 * The name label of this material; texture-safe.
		 * 
		 * @return The name label of this material; texture-safe.
		 */
		public String label() {
			return label;
		}

		/**
		 * Gets the unlocalized name. This cheats and changes all sequences
		 * "_.:" to null and then treats the next character after as an
		 * upper-case sign ('test_label:tomato' => 'testLabelTomato').
		 * Calculated only once and stored in unlocalizedName for speed.
		 * 
		 * @return The unlocalized name.
		 */
		public String unlocalizedName() {
			if (unlocalizedName != null)
				return unlocalizedName;

			StringBuilder result = new StringBuilder();
			boolean flag = false;
			for (char c : label().toCharArray())
				if (c == '_' || c == '.' || c == ':')
					flag = true;
				else if (flag) {
					result.append(Character.toUpperCase(c));
					flag = !flag;
				} else
					result.append(c);
			unlocalizedName = result.toString();
			return unlocalizedName;
		}
	}

	public static class Blocks {

		public static BlockLanteaDecor decorBlock;
		public static BlockLanteaDecorGlass glassDecorBlock;

		public static BlockLanteaDecorStair lanteaSteelDecorStair;

		public static BlockLanteaDecorStair goauldGoldDecorStair;

		public static BlockLanteaDecorStair lanteaPatternedSteelDecorStair;
		public static BlockLanteaDecorStair goauldPatternedGoldDecorStair;

	}

	public static class Render {
		public static TileEntityLanteaDecorGlassRenderer tileEntityLanteaDecorGlassRenderer;
	}

	@Override
	public Set<Module> getDependencies() {
		return EnumSet.of(Module.CORE);
	}

	@Override
	public Set<Module> getLoadDependenciesAfter() {
		return null;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {

	}

	@Override
	public void init(FMLInitializationEvent event) {
		Blocks.decorBlock = RegistrationHelper.registerBlock(BlockLanteaDecor.class, ItemLanteaDecor.class,
				"lanteaDecor");
		Blocks.glassDecorBlock = RegistrationHelper.registerBlock(BlockLanteaDecorGlass.class,
				ItemLanteaDecorGlass.class, "lanteaGlassDecor");
		Blocks.lanteaSteelDecorStair = RegistrationHelper.registerStairDecal("lanteaSteelDecorStair", 1);
		Blocks.lanteaPatternedSteelDecorStair = RegistrationHelper.registerStairDecal("lanteaPatternedSteelDecorStair",
				2);
		Blocks.goauldGoldDecorStair = RegistrationHelper.registerStairDecal("goauldGoldDecorStair", 3);
		Blocks.goauldPatternedGoldDecorStair = RegistrationHelper
				.registerStairDecal("goauldPatternedGoldDecorStair", 4);

		GameRegistry.registerTileEntity(TileEntityLanteaDecorGlass.class, "tileEntityLanteaDecorGlass");
		if (event.getSide() == Side.CLIENT) {
			Render.tileEntityLanteaDecorGlassRenderer = new TileEntityLanteaDecorGlassRenderer();
			RegistrationHelper.addTileEntityRenderer(TileEntityLanteaDecorGlass.class,
					Render.tileEntityLanteaDecorGlassRenderer);
		}

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		// TODO Auto-generated method stub

	}

}
