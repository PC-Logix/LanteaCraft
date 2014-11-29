package lc.common.util.game;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;

public class BlockHelper {
	public static String saveBlock(Block singleton, int metadata) {
		StringBuilder builder = new StringBuilder();
		builder.append(GameData.getBlockRegistry().getNameForObject(singleton));
		if (metadata != 0)
			builder.append(";").append(metadata);
		return builder.toString();
	}

	public static ItemStack loadBlock(String saved) {
		Block block = null;
		int metadata = 0;
		if (saved.indexOf(';') != -1) {
			block = GameData.getBlockRegistry().getObject(saved.substring(0, saved.indexOf(';')));
			metadata = Integer.parseInt(saved.substring(saved.indexOf(';') + 1));
		} else
			block = GameData.getBlockRegistry().getObject(saved);
		return new ItemStack(block, 1, metadata);
	}
}
