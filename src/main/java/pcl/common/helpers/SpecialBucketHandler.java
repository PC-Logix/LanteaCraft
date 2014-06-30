package pcl.common.helpers;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import pcl.lc.module.core.fluid.ItemSpecialBucket;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * SpecialBucketHandler handles Forge onBucketFill from {@link ItemBucket}.
 * 
 * @author AfterLifeLochie
 * 
 */
public class SpecialBucketHandler {

	/**
	 * Map of all Block to ItemSpecialBucket mappings.
	 */
	private HashMap<Block, ItemSpecialBucket> buckets = new HashMap<Block, ItemSpecialBucket>();

	/**
	 * Register a new mapping of {@link Block} type blockOf with an
	 * {@link ItemSpecialBucket} itemResult.
	 * 
	 * @param blockOf
	 *            The fluid host block type.
	 * @param itemResult
	 *            The resulting ItemSpecialBucket when the host block is
	 *            collected in a bucket.
	 */
	public void registerBucketMapping(Block blockOf, ItemSpecialBucket itemResult) {
		buckets.put(blockOf, itemResult);
	}

	@SubscribeEvent
	public void onBucketFill(FillBucketEvent event) {
		ItemStack result = fillCustomBucket(event.world, event.target);
		if (result == null)
			return;
		event.result = result;
		event.setResult(Result.ALLOW);
	}

	/**
	 * Attempts to fill a bucket from the source described.
	 * 
	 * @param world
	 *            The world object.
	 * @param pos
	 *            The position of the fluid source block.
	 * @return The resulting ItemStack from collecting the target fluid in a
	 *         bucket, or null if the fluid cannot be collected with an
	 *         {@link ItemSpecialBucket} registered with the handler.
	 */
	private ItemStack fillCustomBucket(World world, MovingObjectPosition pos) {
		Block block = world.getBlock(pos.blockX, pos.blockY, pos.blockZ);
		for (Entry<Block, ItemSpecialBucket> results : buckets.entrySet())
			if (block.equals(results.getKey())) {
				world.setBlock(pos.blockX, pos.blockY, pos.blockZ, Block.getBlockById(0));
				return new ItemStack(results.getValue());
			}
		return null;
	}

}
