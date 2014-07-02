package pcl.lc.core;

import net.minecraft.util.IIcon;
import pcl.lc.module.core.block.BlockLanteaOre;
import pcl.lc.module.core.item.ItemLanteaOre;
import pcl.lc.module.core.item.ItemLanteaOreBlock;
import pcl.lc.module.core.item.ItemLanteaOreIngot;

/**
 * Internal Ore registry. Contains all required textures to render each ore (see
 * individual declarations of those textures; block: {@link BlockLanteaOre},
 * item: {@link ItemLanteaOre}, ingot: {@link ItemLanteaOreIngot}, block of
 * ingot: {@link ItemLanteaOreBlock}).
 * 
 * @author AfterLifeLochie
 * 
 */
public enum OreTypes {
	NAQUADAH("naquadahOre"), NAQUADRIAH("naqadriahOre"), TRINIUM("triniumOre");

	private String tex;

	private IIcon textureOreBlock;
	private IIcon textureItem;
	private IIcon textureIngotItem;
	private IIcon textureItemAsBlock;

	private OreTypes(String tex) {
		this.tex = tex;
	}

	public static OreTypes fromOrdinal(int ord) {
		if (0 > ord || ord > OreTypes.values().length)
			return null;
		return OreTypes.values()[ord];
	}

	public static OreTypes fromString(String string) {
		for (OreTypes ore : OreTypes.values())
			if (ore.name().equalsIgnoreCase(string))
				return ore;
		return null;
	}

	public String tex() {
		return tex;
	}

	public IIcon getOreTexture() {
		return textureOreBlock;
	}

	public void setOreTexture(IIcon texture) {
		textureOreBlock = texture;
	}

	public IIcon getItemTexture() {
		return textureItem;
	}

	public void setItemTexture(IIcon texture) {
		textureItem = texture;
	}

	public IIcon getIngotItemTexture() {
		return textureIngotItem;
	}

	public void setIngotItemTexture(IIcon texture) {
		textureIngotItem = texture;
	}

	public IIcon getItemAsBlockTexture() {
		return textureItemAsBlock;
	}

	public void setItemAsBlockTexture(IIcon texture) {
		textureItemAsBlock = texture;
	}

}
