package gcewing.sg.blocks;

import gcewing.sg.items.SGRingItem;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class SGPegasusRingBlock extends SGRingBlock {

	public SGPegasusRingBlock(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	static String[] subBlockTitles = { "Pegasus Stargate Ring Block", "Pegasus Stargate Chevron Block", };

	@Override
	void registerSubItemNames() {
		LanguageRegistry registry = LanguageRegistry.instance();
		for (int i = 2; i < SGRingBlock.numSubBlocks; i++) {
			String name = SGRingItem.subItemName(i) + ".name";
			String title = subBlockTitles[i];
			// System.out.printf("SGRingBlock.registerSubItemNames: %s --> %s\n",
			// name, title);
			registry.addStringLocalization(name, "en_US", title);
		}
	}
}
