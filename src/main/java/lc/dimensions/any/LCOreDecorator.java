package lc.dimensions.any;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import lc.api.defs.IDefinitionReference;
import lc.api.world.OreType;
import lc.common.LCLog;
import lc.common.base.generation.LCChunkData;
import lc.common.base.generation.decoration.LCChunkDecoration;
import lc.common.util.game.DataResolver;

public abstract class LCOreDecorator extends LCChunkDecoration {

	protected final OreType type;
	protected final IDefinitionReference blockType;

	private static int genIsolatedOdds = 8;
	private static int maxIsolatedNodes = 4;

	public LCOreDecorator(OreType type, IDefinitionReference blockType) {
		this.type = type;
		this.blockType = blockType;
	}

	@Override
	public String getName() {
		return "LCOreDecorator{" + type.name() + "}";
	}

	@Override
	public void decorateChunk(Random random, World world, Chunk chunk, LCChunkData data) {
		Block blockStone = (Block) Block.blockRegistry.getObject("stone");
		ItemBlock paintItem = (ItemBlock) DataResolver.resolve(blockType).getItem();
		Block paintBlock = paintItem.field_150939_a;
		if (odds(random, genIsolatedOdds) || true) {
			int n = random.nextInt(maxIsolatedNodes) + 1;
			for (int i = 0; i < n; i++) {
				int x = random.nextInt(16);
				int y = random.nextInt(64);
				int z = random.nextInt(16);
				if (getBlock(chunk, x, y, z).equals(blockStone)) {
					generateNode(chunk, world, random, paintBlock, type, x, y, z, 6);
				}
			}
		}
	}

	private boolean odds(Random random, int n) {
		return random.nextInt(n) == 0;
	}

	private Block getBlock(Chunk chunk, int x, int y, int z) {
		return chunk.getBlock(x, y, z);
	}

	private void setBlock(Chunk chunk, int x, int y, int z, Block block, int metadata) {
		chunk.func_150807_a(x, y, z, block, metadata);
	}

	void generateNode(Chunk chunk, World world, Random random, Block block, OreType type, int cx, int cy, int cz,
			int density) {
		Block blockStone = (Block) Block.blockRegistry.getObject("stone");
		int tries = 0;
		main: while (density > 0) {
			int tx = cx, ty = cy, tz = cz;
			expand: while (true) {
				int trans = random.nextInt(6);
				switch (trans) {
				case 0:
					tx++;
					break;
				case 1:
					tx--;
					break;
				case 2:
					ty++;
					break;
				case 3:
					ty--;
					break;
				case 4:
					tz++;
					break;
				case 5:
					tz--;
					break;
				}

				// catch illegal; one vector out of bounds, reset scan
				if (tx > 15 || tz > 15 || 0 > tx || 0 > ty || 0 > tz)
					continue main;
				// expand; this block is already an ore of type id so we can
				// continue our search
				else if (getBlock(chunk, tx, ty, tz).equals(block))
					continue expand;
				else if (getBlock(chunk, tx, ty, tz).equals(blockStone))
					break expand;
				// illegal; detected some other block (air, other ores, etc),
				// so reset the scan and try again.
				else
					break main;
			}

			if (getBlock(chunk, tx, ty, tz).equals(blockStone)) {
				setBlock(chunk, tx, ty, tz, block, type.ordinal());
				density--;
				LCLog.debug("Placed %s at %s %s %s", type, (16 * chunk.xPosition) + tx, ty, (16 * chunk.zPosition) + tz);
				tries = 0;
				continue main;
			}

			if (tries++ > 5)
				break main;
		}
	}

}
