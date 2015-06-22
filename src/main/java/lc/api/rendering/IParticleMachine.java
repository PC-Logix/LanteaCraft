package lc.api.rendering;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

public interface IParticleMachine {

	public void placeParticle(World world, EntityFX particle);

}
