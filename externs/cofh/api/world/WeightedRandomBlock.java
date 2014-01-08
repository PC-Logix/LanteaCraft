
package cofh.api.world;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomItem;

public final class WeightedRandomBlock extends WeightedRandomItem {

    public final int blockId;
    public final int metadata;

    public WeightedRandomBlock(ItemStack ore) {

        super(100);
        this.blockId = ore.itemID;
        this.metadata = ore.getItemDamage();
    }

    public WeightedRandomBlock(ItemStack ore, int weight) {

        super(weight);
        this.blockId = ore.itemID;
        this.metadata = ore.getItemDamage();
    }
}
