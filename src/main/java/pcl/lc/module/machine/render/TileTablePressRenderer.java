package pcl.lc.module.machine.render;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.google.common.primitives.SignedBytes;

import pcl.lc.LanteaCraft;
import pcl.lc.core.ResourceAccess;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileTablePressRenderer extends TileEntitySpecialRenderer {

	private RenderItem itemRenderer;
	private ModelTablePress pressModel;
	private Random random;

	public TileTablePressRenderer() {
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
		pressModel = new ModelTablePress();
		random = new Random();
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float scale) {
		bindTexture(ResourceAccess.getNamedResource("textures/tileentity/table_press_default.png"));
		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glPushMatrix();
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		pressModel.render(0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (tile.getDistanceFrom(this.field_147501_a.field_147560_j, this.field_147501_a.field_147561_k,
				this.field_147501_a.field_147558_l) < 128d) {
			float blockScale = 1.25F;
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glTranslatef((float) x + 0.5f, (float) y + 0.17f, (float) z + 0.34f);
			EntityItem customitem = new EntityItem(field_147501_a.field_147550_f);
			customitem.hoverStart = 0f;
			GL11.glPushMatrix();
			GL11.glRotatef(90, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(blockScale, blockScale, blockScale);
			customitem.setEntityItemStack(new ItemStack(Items.golden_hoe, 1));
			itemRenderer.doRender(customitem, 0, 0, 0, 0, 0);
			GL11.glPopMatrix();
			GL11.glPopMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
		GL11.glEnable(GL11.GL_LIGHTING);
	}

}
