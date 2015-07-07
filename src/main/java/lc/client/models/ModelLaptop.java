package lc.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelLaptop extends ModelBase {

	public static ModelLaptop $ = new ModelLaptop();

	public ModelRenderer base, pivot, lid, kbrd, mouse, scrn;

	public ModelLaptop() {
		textureWidth = 128;
		textureHeight = 32;
		
		base = new ModelRenderer(this, 0, 0);
		pivot = new ModelRenderer(this, 48, 21);
		lid = new ModelRenderer(this, 0, 12);
		kbrd = new ModelRenderer(this, 40, 27);
		mouse = new ModelRenderer(this, 40, 23);
		scrn = new ModelRenderer(this, 0, 23);

		base.addBox(-7.0F, 0.0F, -5.0F, 14, 2, 10);
		base.setRotationPoint(0.0F, 22.0F, 0.0F);
		base.setTextureSize(128, 32);
		base.mirror = true;
		setRotation(base, 0.0F, 0.0F, 0.0F);
		pivot.addBox(-6.0F, -0.5F, -0.5F, 12, 1, 1);
		pivot.setRotationPoint(0.0F, 22.0F, 5.0F);
		pivot.setTextureSize(128, 32);
		pivot.mirror = true;
		setRotation(pivot, 0.0F, 0.0F, 0.0F);
		lid.addBox(-7.0F, -1.0F, -10.0F, 14, 1, 10);
		lid.setRotationPoint(0.0F, 22.0F, 5.0F);
		lid.setTextureSize(128, 32);
		lid.mirror = true;
		setRotation(lid, -1.745329F, 0.0F, 0.0F);
		kbrd.addBox(-6.0F, 0.0F, -2.0F, 11, 1, 4);
		kbrd.setRotationPoint(0.5F, 21.700001F, 1.5F);
		kbrd.setTextureSize(128, 32);
		kbrd.mirror = true;
		setRotation(kbrd, 0.0F, 0.0F, 0.0F);
		mouse.addBox(-2.5F, 0.0F, -1.5F, 5, 1, 3);
		mouse.setRotationPoint(0.0F, 21.700001F, -2.5F);
		mouse.setTextureSize(128, 32);
		mouse.mirror = true;
		setRotation(mouse, 0.0F, 0.0F, 0.0F);
		scrn.addBox(-6.0F, -0.8F, -9.0F, 12, 1, 8);
		scrn.setRotationPoint(0.0F, 22.0F, 5.0F);
		scrn.setTextureSize(128, 32);
		scrn.mirror = true;
		setRotation(scrn, -1.745329F, 0.0F, 0.0F);
	}

	public void renderAll(float lidAngle) {
		lid.rotateAngleX = lidAngle;
		scrn.rotateAngleX = lidAngle;
		base.render(0.0625F);
		pivot.render(0.0625F);
		lid.render(0.0625F);
		kbrd.render(0.0625F);
		mouse.render(0.0625F);
		scrn.render(0.0625F);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, null);
	}

}
