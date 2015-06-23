package lc.client.opengl;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class LCEntityFX extends EntityFX {

	protected LCEntityFX(World world, double d0, double d1, double d2) {
		super(world, d0, d1, d2, 0.0d, 0.0d, 0.0d);
	}

	protected LCEntityFX(World world, double d0, double d1, double d2, double d3, double d4, double d5) {
		super(world, d0, d1, d2, d3, d4, d5);
	}

	public abstract ResourceLocation getTextureForRender();

}
