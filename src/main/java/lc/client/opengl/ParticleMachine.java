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
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
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

	private HashMap<Integer, HashMap<Integer, ArrayList<LCEntityFX>>> particles;

	/** Default constructor */
	public ParticleMachine() {
		LCRuntime.runtime.ticks().register(this);
		MinecraftForge.EVENT_BUS.register(this);
		particles = new HashMap<Integer, HashMap<Integer, ArrayList<LCEntityFX>>>();
		for (int i = 0; i < 4; i++)
			particles.put(i, new HashMap<Integer, ArrayList<LCEntityFX>>());
	}

	@Override
	public void placeParticle(World theWorld, Object object) {
		int dimension = theWorld.provider.dimensionId;
		if (!(object instanceof LCEntityFX))
			throw new IllegalArgumentException("Must provide LCEntityFX decendant.");
		LCEntityFX entity = (LCEntityFX) object;
		if (!particles.get(entity.getFXLayer()).containsKey(dimension))
			particles.get(entity.getFXLayer()).put(dimension, new ArrayList<LCEntityFX>());
		particles.get(entity.getFXLayer()).get(dimension).add((LCEntityFX) entity);
	}

	@Override
	public void think(Side what) {
		if (what == Side.CLIENT)
			thinkParticles();
	}

	private void thinkParticles() {
		World theWorld = Minecraft.getMinecraft().theWorld;
		if (theWorld == null)
			return;
		int dimension = theWorld.provider.dimensionId;
		for (int layer = 0; layer < 4; layer++) {
			HashMap<Integer, ArrayList<LCEntityFX>> objects = particles.get(layer);
			if (objects.containsKey(dimension)) {
				ArrayList<LCEntityFX> entities = objects.get(dimension);
				Iterator<LCEntityFX> iter = entities.iterator();
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
		if (theWorld == null)
			return;
		Entity player = Minecraft.getMinecraft().thePlayer;
		int dimension = theWorld.provider.dimensionId;
		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.0044F);
		Tessellator tessellator = Tessellator.instance;
		TextureManager renderer = Minecraft.getMinecraft().renderEngine;

		for (int layer = 0; layer < 4; layer++) {
			HashMap<Integer, ArrayList<LCEntityFX>> objects = particles.get(layer);
			if (objects.containsKey(dimension)) {
				ArrayList<LCEntityFX> list = objects.get(dimension);
				if (list.size() == 0)
					continue;
				GL11.glPushMatrix();
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
				Iterator<LCEntityFX> entities = list.iterator();
				while (entities.hasNext()) {
					LCEntityFX entity = entities.next();

					float f1 = ActiveRenderInfo.rotationX;
					float f2 = ActiveRenderInfo.rotationZ;
					float f3 = ActiveRenderInfo.rotationYZ;
					float f4 = ActiveRenderInfo.rotationXY;
					float f5 = ActiveRenderInfo.rotationXZ;

					EntityFX.interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * frame;
					EntityFX.interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * frame;
					EntityFX.interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * frame;
					tessellator.startDrawingQuads();
					tessellator.setBrightness(entity.getBrightnessForRender(frame));
					renderer.bindTexture(entity.getTextureForRender());
					entity.renderParticle(tessellator, frame, f1, f5, f2, f3, f4);
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
