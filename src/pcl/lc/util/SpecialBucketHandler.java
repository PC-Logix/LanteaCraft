package pcl.lc.util;

import java.util.HashMap;
import java.util.Map.Entry;

import pcl.lc.fluids.ItemSpecialBucket;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class SpecialBucketHandler {

	public void registerBucketMapping(Block blockOf, ItemSpecialBucket itemResult) {
		buckets.put(blockOf, itemResult);
	}

	private HashMap<Block, ItemSpecialBucket> buckets = new HashMap<Block, ItemSpecialBucket>();

	@ForgeSubscribe
	public void onBucketFill(FillBucketEvent event) {
		ItemStack result = fillCustomBucket(event.world, event.target);
		if (result == null) return;
		event.result = result;
		event.setResult(Result.ALLOW);
	}

	private ItemStack fillCustomBucket(World world, MovingObjectPosition pos) {
		int blockID = world.getBlockId(pos.blockX, pos.blockY, pos.blockZ);
		for (Entry<Block, ItemSpecialBucket> results : buckets.entrySet()) {
			if (blockID == results.getKey().blockID) {
				world.setBlock(pos.blockX, pos.blockY, pos.blockZ, 0);
				return new ItemStack(results.getValue());
			}
		}
		return null;
	}

}
