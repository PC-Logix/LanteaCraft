package gcewing.sg.multiblock;

import net.minecraft.block.Block;
import gcewing.sg.util.ImmutableTuple;

public interface IStructureConfiguration {

	public ImmutableTuple<Integer, Integer, Integer> getStructureDimensions();

	public ImmutableTuple<Integer, Integer, Integer> getStructureCenter();

	public int[][] getStructureLayout();

	public Block[] getBlockMapping();

}
