package lc.api.world;

import net.minecraft.util.IIcon;

public enum OreType {
	NAQUADAH("naquadahOre"), NAQUADRIAH("naqadriahOre"), TRINIUM("triniumOre");

	private String tex;

	private IIcon textureOreBlock;
	private IIcon textureItem;
	private IIcon textureAlloyItem;
	private IIcon textureAlloyBlock;

	private OreType(String tex) {
		this.tex = tex;
	}

	public static OreType fromOrdinal(int ord) {
		if (0 > ord || ord > OreType.values().length)
			return null;
		return OreType.values()[ord];
	}

	public static OreType fromString(String string) {
		for (OreType ore : OreType.values())
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
		return textureAlloyItem;
	}

	public void setIngotItemTexture(IIcon texture) {
		textureAlloyItem = texture;
	}

	public IIcon getItemAsBlockTexture() {
		return textureAlloyBlock;
	}

	public void setItemAsBlockTexture(IIcon texture) {
		textureAlloyBlock = texture;
	}
}
