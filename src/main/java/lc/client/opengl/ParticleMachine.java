package lc.client.opengl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import lc.LCRuntime;
import lc.api.event.ITickEventHandler;
import lc.api.rendering.IParticleMachine;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

public class ParticleMachine implements ITickEventHandler, IParticleMachine {

	private HashMap<Integer, ArrayList<EntityFX>> particles = new HashMap<Integer, ArrayList<EntityFX>>();

	public ParticleMachine() {
		LCRuntime.runtime.ticks().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void placeParticle(World theWorld, EntityFX entity) {
		int dimension = theWorld.provider.dimensionId;
		if (!particles.containsKey(dimension))
			particles.put(dimension, new ArrayList<EntityFX>());
		particles.get(dimension).add(entity);
	}

	@Override
	public void think(Side what) {
		if (what == Side.CLIENT)
			thinkParticles();
	}

	private void thinkParticles() {
		World theWorld = Minecraft.getMinecraft().theWorld;
		int dimension = theWorld.provider.dimensionId;
		if (!particles.containsKey(dimension))
			return;
		ArrayList<EntityFX> entities = particles.get(dimension);
		Iterator<EntityFX> iter = entities.iterator();
		while (iter.hasNext()) {
			EntityFX entity = iter.next();
			if (entity != null) {
				entity.onUpdate();
				if (entity == null || entity.isDead)
					iter.remove();
			} else
				iter.remove();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderParticles(RenderWorldLastEvent event) {

	}

}
