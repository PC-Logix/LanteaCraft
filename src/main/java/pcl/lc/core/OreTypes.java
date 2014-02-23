package pcl.lc.core;

import pcl.lc.blocks.BlockLanteaOre;
import pcl.lc.items.ItemLanteaOre;
import pcl.lc.items.ItemLanteaOreBlock;
import pcl.lc.items.ItemLanteaOreIngot;
import net.minecraft.util.Icon;

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

	private Icon textureOreBlock;
	private Icon textureItem;
	private Icon textureIngotItem;
	private Icon textureItemAsBlock;

	private OreTypes(String tex) {
		this.tex = tex;
	}

	public static OreTypes fromOrdinal(int ord) {
		if (0 > ord || ord > OreTypes.values().length)
			return null;
		return OreTypes.values()[ord];
	}

	public String tex() {
		return tex;
	}

	public Icon getOreTexture() {
		return textureOreBlock;
	}

	public void setOreTexture(Icon texture) {
		this.textureOreBlock = texture;
	}

	public Icon getItemTexture() {
		return textureItem;
	}

	public void setItemTexture(Icon texture) {
		this.textureItem = texture;
	}

	public Icon getIngotItemTexture() {
		return textureIngotItem;
	}

	public void setIngotItemTexture(Icon texture) {
		this.textureIngotItem = texture;
	}

	public Icon getItemAsBlockTexture() {
		return textureItemAsBlock;
	}

	public void setItemAsBlockTexture(Icon texture) {
		this.textureItemAsBlock = texture;
	}

}
