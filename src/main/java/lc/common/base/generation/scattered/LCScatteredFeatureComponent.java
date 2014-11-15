package lc.common.base.generation.scattered;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

/**
 * LanteaCraft scattered feature generation stub.
 *
 * @author AfterLifeLochie
 */
public abstract class LCScatteredFeatureComponent extends StructureComponent {

	public LCScatteredFeatureComponent() {
	}

	/**
	 * Save structure data to NBT tag specified.
	 */
	@Override
	protected abstract void func_143012_a(NBTTagCompound nbttagcompound);

	/**
	 * Load structure data from NBT tag specified.
	 */
	@Override
	protected abstract void func_143011_b(NBTTagCompound nbttagcompound);

	/**
	 * Build the component into the target world using the random number
	 * generator provided.
	 */
	@Override
	public abstract boolean addComponentParts(World world, Random random, StructureBoundingBox structureboundingbox);

}
