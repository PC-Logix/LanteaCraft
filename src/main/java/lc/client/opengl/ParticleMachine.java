package lc.client.opengl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import lc.LCRuntime;
import lc.api.event.ITickEventHandler;
import lc.api.rendering.IParticleMachine;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

public class ParticleMachine implements ITickEventHandler, IParticleMachine {

	private HashMap<Integer, HashMap<Integer, ArrayList<EntityFX>>> particles;

	public ParticleMachine() {
		LCRuntime.runtime.ticks().register(this);
		MinecraftForge.EVENT_BUS.register(this);
		particles = new HashMap<Integer, HashMap<Integer, ArrayList<EntityFX>>>();
		for (int i = 0; i < 4; i++)
			particles.put(i, new HashMap<Integer, ArrayList<EntityFX>>());
	}

	@Override
	public void placeParticle(World theWorld, EntityFX entity) {
		int dimension = theWorld.provider.dimensionId;
		if (!particles.get(entity.getFXLayer()).containsKey(dimension))
			particles.get(entity.getFXLayer()).put(dimension, new ArrayList<EntityFX>());
		particles.get(entity.getFXLayer()).get(dimension).add(entity);
	}

	@Override
	public void think(Side what) {
		if (what == Side.CLIENT)
			thinkParticles();
	}

	private void thinkParticles() {
		World theWorld = Minecraft.getMinecraft().theWorld;
		int dimension = theWorld.provider.dimensionId;
		for (int i = 0; i < 4; i++) {
			HashMap<Integer, ArrayList<EntityFX>> layer = particles.get(i);
			if (layer.containsKey(dimension)) {
				ArrayList<EntityFX> entities = layer.get(dimension);
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
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderParticles(RenderWorldLastEvent event) {
		float frame = event.partialTicks;
		World theWorld = Minecraft.getMinecraft().theWorld;
		int dimension = theWorld.provider.dimensionId;
		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthMask(false);
		Tessellator tessellator = Tessellator.instance;
		for (int i = 0; i < 4; i++) {
			HashMap<Integer, ArrayList<EntityFX>> layer = particles.get(i);
			if (layer.containsKey(dimension)) {
				ArrayList<EntityFX> list = layer.get(dimension);
				if (list.size() == 0)
					continue;
				Iterator<EntityFX> entities = list.iterator();
				while (entities.hasNext()) {
					EntityFX entity = entities.next();
					GL11.glPushMatrix();
					float rotationX = ActiveRenderInfo.rotationX, rotationZ = ActiveRenderInfo.rotationZ;
					float rotationYZ = ActiveRenderInfo.rotationYZ, rotationXY = ActiveRenderInfo.rotationXY, rotationXZ = ActiveRenderInfo.rotationXZ;
					EntityFX.interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * frame;
					EntityFX.interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * frame;
					EntityFX.interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * frame;
					tessellator.startDrawingQuads();
					tessellator.setBrightness(entity.getBrightnessForRender(frame));
					entity.renderParticle(tessellator, frame, rotationX, rotationXZ, rotationZ, rotationYZ, rotationXY);
					tessellator.draw();
					GL11.glPopMatrix();
				}
			}
		}
		GL11.glPopMatrix();
	}
}
