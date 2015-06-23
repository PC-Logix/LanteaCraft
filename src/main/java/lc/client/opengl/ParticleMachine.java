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

/**
 * Client-side particle on-demand rendering system
 * 
 * @author AfterLifeLochie
 *
 */
public class ParticleMachine implements ITickEventHandler, IParticleMachine {

	private HashMap<Integer, HashMap<Integer, ArrayList<EntityFX>>> particles;

	/** Default constructor */
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
		for (int layer = 0; layer < 4; layer++) {
			HashMap<Integer, ArrayList<EntityFX>> objects = particles.get(layer);
			if (objects.containsKey(dimension)) {
				ArrayList<EntityFX> entities = objects.get(dimension);
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
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.0044F);
		Tessellator tessellator = Tessellator.instance;
		for (int layer = 0; layer < 4; layer++) {
			HashMap<Integer, ArrayList<EntityFX>> objects = particles.get(layer);
			if (objects.containsKey(dimension)) {
				GL11.glPushMatrix();
				ArrayList<EntityFX> list = objects.get(dimension);
				if (list.size() == 0)
					continue;
				switch (layer) {
				case 0:
				case 2:
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
					break;
				case 1:
				case 3:
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					break;
				}
				Iterator<EntityFX> entities = list.iterator();
				while (entities.hasNext()) {
					EntityFX entity = entities.next();
					float rotX = ActiveRenderInfo.rotationX, rotZ = ActiveRenderInfo.rotationZ;
					float rotYZ = ActiveRenderInfo.rotationYZ, rotXY = ActiveRenderInfo.rotationXY, rotXZ = ActiveRenderInfo.rotationXZ;
					EntityFX.interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * frame;
					EntityFX.interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * frame;
					EntityFX.interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * frame;
					tessellator.startDrawingQuads();
					tessellator.setBrightness(entity.getBrightnessForRender(frame));
					entity.renderParticle(tessellator, frame, rotX, rotXZ, rotZ, rotYZ, rotXY);
					tessellator.draw();
				}
				GL11.glPopMatrix();
			}
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glPopMatrix();
	}
}
