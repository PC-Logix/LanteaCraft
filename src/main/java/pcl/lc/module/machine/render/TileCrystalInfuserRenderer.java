package pcl.lc.module.machine.render;

import java.util.Random;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import pcl.lc.api.internal.LanteaInternalRecipe;
import pcl.lc.core.ResourceAccess;
import pcl.lc.module.machine.tile.TileCrystalInfuser;

import com.google.common.primitives.SignedBytes;

public class TileCrystalInfuserRenderer extends TileEntitySpecialRenderer {

	private RenderItem itemRenderer;
	private ModelCrystalInfuser infuserModel;
	private Random random;

	public TileCrystalInfuserRenderer() {
		itemRenderer = new RenderItem() {
			@Override
			public byte getMiniBlockCount(ItemStack stack, byte original) {
				return SignedBytes.saturatedCast(Math.min(stack.stackSize / 32, 15) + 1);
			}

			@Override
			public byte getMiniItemCount(ItemStack stack, byte original) {
				return SignedBytes.saturatedCast(Math.min(stack.stackSize / 32, 7) + 1);
			}

			@Override
			public boolean shouldBob() {
				return false;
			}

			@Override
			public boolean shouldSpreadItems() {
				return false;
			}
		};
		itemRenderer.setRenderManager(RenderManager.instance);
		infuserModel = new ModelCrystalInfuser();
		random = new Random();
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float scale) {
		TileCrystalInfuser infuser = (TileCrystalInfuser) tile;
		bindTexture(ResourceAccess.getNamedResource("textures/tileentity/crystal_infuser_default.png"));
		GL11.glPushMatrix();
		infuserModel.preTile(tile, x, y, z, scale);
		infuserModel.render(0.0625F);
		infuserModel.postTile(tile, x, y, z, scale);
		GL11.glPopMatrix();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (tile.getDistanceFrom(field_147501_a.field_147560_j, field_147501_a.field_147561_k,
				field_147501_a.field_147558_l) < 128d) {

			ItemStack renderItem = null;
			ItemStack renderNextItem = null;
			if (infuser.getInventory().getStackInSlot(0) != null && infuser.hasRecipe()) {
				LanteaInternalRecipe recipe = infuser.getCurrentRecipe();
				if (recipe != null) {
					renderItem = infuser.getInventory().getStackInSlot(0).copy();
					renderNextItem = recipe.product(0).copy();
				}
			}

			if (renderItem != null)
				renderItem.stackSize = 1;
			if (renderNextItem != null)
				renderNextItem.stackSize = 1;

			if (renderItem != null && renderItem.getItem() != null) {
				float blockScale = 0.70F;
				float time_d = (System.currentTimeMillis() & 0xFFF) / (float) (0xFFF);
				float h = (float) Math.sin(time_d * Math.PI) * 0.1f;
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glTranslatef((float) x + 0.685f, (float) y + 0.6f + h, (float) z + 0.5f);

				EntityItem customitem = new EntityItem(field_147501_a.field_147550_f);
				customitem.hoverStart = 0f;
				GL11.glPushMatrix();
				GL11.glRotatef(90, 0.0F, 1.0F, 0.0F);
				if (renderNextItem != null) {
					float dt = Math.min(0.7f * (infuser.getProgress() / 10.0f), 0.7f);
					GL11.glPushMatrix();
					GL11.glScalef(0.7f - dt, 0.7f - dt, 0.7f - dt);
					customitem.setEntityItemStack(renderItem);
					itemRenderer.doRender(customitem, 0, 0, 0, 0, 0);
					GL11.glPopMatrix();

					GL11.glPushMatrix();
					GL11.glScalef(dt, dt, dt);
					customitem.setEntityItemStack(renderNextItem);
					itemRenderer.doRender(customitem, 0, 0, 0, 0, 0);
					GL11.glPopMatrix();
				} else {
					GL11.glScalef(blockScale, blockScale, blockScale);

					customitem.setEntityItemStack(renderItem);
					itemRenderer.doRender(customitem, 0, 0, 0, 0, 0);
				}

				GL11.glPopMatrix();
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glPopMatrix();
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

				if (infuser.hasRecipe() && infuser.getProgress() > 0.0f) {
					int q = 1 + random.nextInt(12);
					while (q-- > 0) {
						int npillar = random.nextInt(2);
						float height = 0.01f * random.nextInt(33);
						float theight = 0.1f + h;
						float dk = 0.05f;
						GL11.glPushMatrix();
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glDisable(GL11.GL_TEXTURE_2D);
						float dlight = (random.nextInt(84) / 256.0f);
						GL11.glColor4f(28.0f / 256.0f, 172.0f / 256.0f + dlight, 120.0f / 256.0f,
								0.25F + (0.025f * random.nextInt(10)));
						GL11.glTranslatef((float) x + 0.69f, (float) y + 0.5f, (float) z + 0.25f);
						GL11.glBegin(GL11.GL_QUADS);

						if (npillar == 0) {
							GL11.glVertex3f(0.0f, height, 0.0f);
							GL11.glVertex3f(0.0f, 0.1f + theight, 0.25f);
							GL11.glVertex3f(0.0f, 0.1f + theight, 0.25f);
							GL11.glVertex3f(0.0f, height + dk, 0.0f);

							GL11.glVertex3f(0.0f, height, 0.0f);
							GL11.glVertex3f(0.0f, height + dk, 0.0f);
							GL11.glVertex3f(0.0f, 0.1f + theight, 0.25f);
							GL11.glVertex3f(0.0f, 0.1f + theight, 0.25f);
						} else {
							GL11.glVertex3f(0.0f, height, 0.5f);
							GL11.glVertex3f(0.0f, height + dk, 0.5f);
							GL11.glVertex3f(0.0f, 0.1f + theight, 0.25f);
							GL11.glVertex3f(0.0f, 0.1f + theight, 0.25f);

							GL11.glVertex3f(0.0f, height, 0.5f);
							GL11.glVertex3f(0.0f, 0.1f + theight, 0.25f);
							GL11.glVertex3f(0.0f, 0.1f + theight, 0.25f);
							GL11.glVertex3f(0.0f, height + dk, 0.5f);
						}

						GL11.glEnd();
						GL11.glDisable(GL11.GL_BLEND);
						GL11.glEnable(GL11.GL_TEXTURE_2D);
						GL11.glPopMatrix();
					}
				}
			}
		}
		GL11.glEnable(GL11.GL_LIGHTING);

	}

}
