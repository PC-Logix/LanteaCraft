package lc.client.opengl;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class LCEntityFX extends EntityFX {

	protected LCEntityFX(World world, double x, double y, double z) {
		super(world, x, y, z, 0.0d, 0.0d, 0.0d);
	}

	protected LCEntityFX(World world, double x, double y, double z, double mx, double my, double mz) {
		super(world, x, y, z, mx, my, mz);
	}

	public abstract ResourceLocation getTextureForRender();

}
