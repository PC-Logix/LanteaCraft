package gcewing.sg.blocks;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import gcewing.sg.items.SGPegasusRingItem;
import gcewing.sg.items.SGRingItem;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class SGPegasusRingBlock extends SGRingBlock {

	public SGPegasusRingBlock(int id) {
		super(id);
		setHardness(1.5F);
		registerSubItemNames();
	}

	static String[] subBlockTitles = { "Pegasus Stargate Ring Block", "Pegasus Stargate Chevron Block", };

	@Override
	void registerSubItemNames() {
		LanguageRegistry registry = LanguageRegistry.instance();
		for (int i = 2; i < SGPegasusRingBlock.numSubBlocks; i++) {
			String name = SGPegasusRingItem.subItemName(i) + ".name";
			String title = subBlockTitles[i];
			System.out.printf("SGPegasusRingBlock.registerSubItemNames: %s --> %s\n", name, title);
			registry.addStringLocalization(name, "en_US", title);
		}
	}
}
