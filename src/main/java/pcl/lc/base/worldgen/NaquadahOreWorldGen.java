package pcl.lc.base.worldgen;

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import org.apache.logging.log4j.Level;

import pcl.lc.LanteaCraft;
import pcl.lc.cfg.ConfigHelper;
import pcl.lc.cfg.ConfigList;
import pcl.lc.cfg.ConfigNode;
import pcl.lc.cfg.ModuleConfig;
import pcl.lc.core.OreTypes;
import pcl.lc.module.ModuleCore;
import cpw.mods.fml.common.IWorldGenerator;

public class NaquadahOreWorldGen implements IWorldGenerator {

	private static int genUnderLavaOdds = 4;
	private static int maxNodesUnderLava = 8;
	private static int genIsolatedOdds = 8;
	private static int maxIsolatedNodes = 4;
	private static EnumSet<OreTypes> enabledOreSpawns = EnumSet.of(OreTypes.NAQUADAH, OreTypes.TRINIUM);

	public static void configure(ModuleConfig cfg) {
		ConfigList enabledOres = (ConfigList) ConfigHelper.findConfigByClass(cfg, "EnabledOres");
		if (enabledOres != null) {
			enabledOreSpawns.clear();
			for (ConfigNode node : enabledOres.children())
				if (node.name().equalsIgnoreCase("Ore") && node.parameters().containsKey("name")
						&& node.parameters().get("name") instanceof String) {
					OreTypes typeof = OreTypes.fromString(node.parameters().get("name").toString());
					if (typeof != null)
						enabledOreSpawns.add(typeof);
				}
		} else {
			enabledOres = new ConfigList("EnabledOres", "Enabled ore-generation list", cfg);
			for (OreTypes typeof : enabledOreSpawns) {
				ConfigNode spawnrule = new ConfigNode("Ore", enabledOres);
				spawnrule.parameters().put("name", typeof.name());
				enabledOres.children().add(spawnrule);
				spawnrule.modify();
			}
			cfg.children().add(enabledOres);
			enabledOres.modify();
		}

		genUnderLavaOdds = Integer.parseInt(ConfigHelper.getOrSetParam(cfg, "OreGenerator", "genUnderLava", "odds",
				"Odds and density of generation under lava", genUnderLavaOdds).toString());
		maxNodesUnderLava = Integer.parseInt(ConfigHelper.getOrSetParam(cfg, "OreGenerator", "genUnderLava", "size",
				"Odds and density of generation under lava", maxNodesUnderLava).toString());

		genIsolatedOdds = Integer.parseInt(ConfigHelper.getOrSetParam(cfg, "OreGenerator", "genIsolated", "odds",
				"Odds and density of generation of above lava nodes", genUnderLavaOdds).toString());
		maxIsolatedNodes = Integer.parseInt(ConfigHelper.getOrSetParam(cfg, "OreGenerator", "genIsolated", "size",
				"Odds and density of generation of above lava nodes", genUnderLavaOdds).toString());
	}

	public void readChunk(ChunkData data, Chunk chunk) {
		for (OreTypes typeof : enabledOreSpawns)
			if (!data.getOreGenerated(typeof))
				generateChunk(typeof, chunk.worldObj, chunk);
	}

	private Random requestRandomFor(int x, int z, World world, int oreTypeof) {
		return new Random((1 + oreTypeof) * (x + z) ^ world.getSeed());
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
			IChunkProvider chunkProvider) {
		Chunk target = world.getChunkFromChunkCoords(chunkX, chunkZ);
		ChunkData data = ChunkData.forChunk(target);
		for (OreTypes typeof : enabledOreSpawns)
			if (!data.getOreGenerated(typeof))
				generateChunk(typeof, world, target);
	}

	public void regenerate(Chunk chunk) {
		ChunkData data = ChunkData.forChunk(chunk);
		for (OreTypes typeof : enabledOreSpawns)
			if (!data.getOreGenerated(typeof))
				generateChunk(typeof, chunk.worldObj, chunk);
	}

	private Block getBlock(Chunk chunk, int x, int y, int z) {
		return chunk.getBlock(x, y, z);
	}

	private void setBlock(Chunk chunk, int x, int y, int z, Block block, int metadata) {
		chunk.func_150807_a(x, y, z, block, metadata);
	}

	void generateNode(Chunk chunk, World world, Random random, Block block, int metadata, int cx, int cy, int cz,
			int density) {
		LanteaCraft.getLogger().log(
				Level.TRACE,
				String.format("Node generator building node around %s %s %s with density %s typeof %s", cx
						+ (16 * chunk.getChunkCoordIntPair().chunkXPos), cy, cz
						+ (16 * chunk.getChunkCoordIntPair().chunkZPos), density, metadata));
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
				else if (getBlock(chunk, tx, ty, tz).equals(ModuleCore.Blocks.lanteaOre))
					continue expand;
				else if (getBlock(chunk, tx, ty, tz).equals(blockStone))
					break expand;
				// illegal; detected some other block (air, other ores, etc),
				// so reset the scan and try again.
				else
					break main;
			}

			if (getBlock(chunk, tx, ty, tz).equals(blockStone)) {
				setBlock(chunk, tx, ty, tz, block, metadata);
				density--;
				tries = 0;
				continue main;
			}

			if (tries++ > 5)
				break main;
		}
	}

	private boolean odds(Random random, int n) {
		return random.nextInt(n) == 0;
	}

	void generateChunk(OreTypes typeof, World world, Chunk chunk) {
		Random random = requestRandomFor(chunk.getChunkCoordIntPair().chunkXPos,
				chunk.getChunkCoordIntPair().chunkZPos, world, typeof.ordinal());
		Block blockStone = (Block) Block.blockRegistry.getObject("stone");
		if (odds(random, genIsolatedOdds) || true) {
			int n = random.nextInt(maxIsolatedNodes) + 1;
			for (int i = 0; i < n; i++) {
				int x = random.nextInt(16);
				int y = random.nextInt(64);
				int z = random.nextInt(16);
				if (getBlock(chunk, x, y, z).equals(blockStone)) {
					LanteaCraft.getLogger().log(Level.TRACE,
							String.format("Attempting to place Naquadah node at %s %s %s", x, y, z));
					generateNode(chunk, world, random, ModuleCore.Blocks.lanteaOre, typeof.ordinal(), x, y, z, 6);
				}
			}
		}
		ChunkData.forChunk(chunk).markOreGenerated(OreTypes.NAQUADAH);
	}

}
