package pcl.lc.core;

import net.minecraft.util.Icon;

public enum OreTypes {
	NAQUADAH("naquadahOre"), NAQAHDRIAH("naqahdriahOre"), TRINIUM("triniumOre");

	private String tex;

	private Icon textureOreBlock;
	private Icon textureItem;
	private Icon textureIngotItem;
	private Icon textureItemAsBlock;

	private OreTypes(String tex) {
		this.tex = tex;
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
