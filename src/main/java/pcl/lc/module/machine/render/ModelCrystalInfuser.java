package pcl.lc.module.machine.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelCrystalInfuser extends ModelBase {
	ModelRenderer MachineControls;
	ModelRenderer MachineBody;
	ModelRenderer MachineFrontBase;
	ModelRenderer MachineTray;
	ModelRenderer MachineArmLeft;
	ModelRenderer MachineArmRight;
	ModelRenderer MachineItemHolderLeft;
	ModelRenderer MachineItemHolderRight;
	ModelRenderer MachineTrayCoverFW;
	ModelRenderer MachineTrayCoverBW;
	ModelRenderer MachineTrayCoverLW;
	ModelRenderer MachineTrayCoverRW;
	ModelRenderer MachineTrayCoverTW;

	public ModelCrystalInfuser() {
		textureWidth = 128;
		textureHeight = 128;

		MachineControls = new ModelRenderer(this, 42, 23);
		MachineControls.addBox(-2F, -8F, -8F, 2, 8, 14);
		MachineControls.setRotationPoint(8F, 22F, 1F);
		MachineControls.setTextureSize(128, 128);
		MachineControls.mirror = true;
		setRotation(MachineControls, 0F, 0F, -0.6340829F);
		MachineBody = new ModelRenderer(this, 0, 0);
		MachineBody.addBox(0F, 0F, 0F, 11, 7, 16);
		MachineBody.setRotationPoint(-8.02F, 17F, -8F);
		MachineBody.setTextureSize(128, 128);
		MachineBody.mirror = true;
		setRotation(MachineBody, 0F, 0F, 0F);
		MachineFrontBase = new ModelRenderer(this, 0, 23);
		MachineFrontBase.addBox(11F, 6F, 0F, 5, 1, 16);
		MachineFrontBase.setRotationPoint(-8.02F, 17F, -8F);
		MachineFrontBase.setTextureSize(128, 128);
		MachineFrontBase.mirror = true;
		setRotation(MachineFrontBase, 0F, 0F, 0F);
		MachineTray = new ModelRenderer(this, 0, 40);
		MachineTray.addBox(0.5F, -1F, 3.5F, 9, 1, 9);
		MachineTray.setRotationPoint(-8.02F, 17F, -8F);
		MachineTray.setTextureSize(128, 128);
		MachineTray.mirror = true;
		setRotation(MachineTray, 0F, 0F, 0F);
		MachineArmLeft = new ModelRenderer(this, 36, 40);
		MachineArmLeft.addBox(-0.5F, -5.5F, -4F, 1, 6, 1);
		MachineArmLeft.setRotationPoint(-3F, 15.5F, 0F);
		MachineArmLeft.setTextureSize(128, 128);
		MachineArmLeft.mirror = true;
		setRotation(MachineArmLeft, 0F, 0F, 0F);
		MachineArmRight = new ModelRenderer(this, 36, 40);
		MachineArmRight.addBox(-0.5F, -5.5F, 3F, 1, 6, 1);
		MachineArmRight.setRotationPoint(-3F, 15.5F, 0F);
		MachineArmRight.setTextureSize(128, 128);
		MachineArmRight.mirror = true;
		setRotation(MachineArmRight, 0F, 0F, 0F);
		MachineItemHolderLeft = new ModelRenderer(this, 36, 40);
		MachineItemHolderLeft.addBox(-0.5F, -0.5F, -1F, 1, 1, 1);
		MachineItemHolderLeft.setRotationPoint(-3F, 15.5F, -0.5F);
		MachineItemHolderLeft.setTextureSize(128, 128);
		MachineItemHolderLeft.mirror = true;
		setRotation(MachineItemHolderLeft, 0F, 0F, 0F);
		MachineItemHolderRight = new ModelRenderer(this, 36, 40);
		MachineItemHolderRight.addBox(-0.5F, -0.5F, 0F, 1, 1, 1);
		MachineItemHolderRight.setRotationPoint(-3F, 15.5F, 0.5F);
		MachineItemHolderRight.setTextureSize(128, 128);
		MachineItemHolderRight.mirror = true;
		setRotation(MachineItemHolderRight, 0F, 0F, 0F);
		MachineTrayCoverFW = new ModelRenderer(this, 38, -7);
		MachineTrayCoverFW.addBox(9F, -7F, -4.5F, 0, 7, 9);
		MachineTrayCoverFW.setRotationPoint(-7.5F, 16F, 0F);
		MachineTrayCoverFW.setTextureSize(128, 128);
		MachineTrayCoverFW.mirror = true;
		setRotation(MachineTrayCoverFW, 0F, 0F, -0.7853982F);
		MachineTrayCoverBW = new ModelRenderer(this, 38, 0);
		MachineTrayCoverBW.addBox(0F, -7F, -4.5F, 0, 7, 9);
		MachineTrayCoverBW.setRotationPoint(-7.5F, 16F, 0F);
		MachineTrayCoverBW.setTextureSize(128, 128);
		MachineTrayCoverBW.mirror = true;
		setRotation(MachineTrayCoverBW, 0F, 0F, -0.7853982F);
		MachineTrayCoverLW = new ModelRenderer(this, 56, 9);
		MachineTrayCoverLW.addBox(0F, -7F, -4.5F, 9, 7, 0);
		MachineTrayCoverLW.setRotationPoint(-7.5F, 16F, 0F);
		MachineTrayCoverLW.setTextureSize(128, 128);
		MachineTrayCoverLW.mirror = true;
		setRotation(MachineTrayCoverLW, 0F, 0F, -0.7853982F);
		MachineTrayCoverRW = new ModelRenderer(this, 56, 2);
		MachineTrayCoverRW.addBox(0F, -7F, 4.5F, 9, 7, 0);
		MachineTrayCoverRW.setRotationPoint(-7.5F, 16F, 0F);
		MachineTrayCoverRW.setTextureSize(128, 128);
		MachineTrayCoverRW.mirror = true;
		setRotation(MachineTrayCoverRW, 0F, 0F, -0.7853982F);
		MachineTrayCoverTW = new ModelRenderer(this, 65, 0);
		MachineTrayCoverTW.addBox(0F, -7F, -4.5F, 9, 0, 9);
		MachineTrayCoverTW.setRotationPoint(-7.5F, 16F, 0F);
		MachineTrayCoverTW.setTextureSize(128, 128);
		MachineTrayCoverTW.mirror = true;
		setRotation(MachineTrayCoverTW, 0F, 0F, -0.7853982F);
	}

	public void render(float f5) {
		MachineControls.render(f5);
		MachineBody.render(f5);
		MachineFrontBase.render(f5);
		MachineTray.render(f5);
		MachineArmLeft.render(f5);
		MachineArmRight.render(f5);
		MachineItemHolderLeft.render(f5);
		MachineItemHolderRight.render(f5);
		//MachineTrayCoverFW.render(f5);
		//MachineTrayCoverBW.render(f5);
		//MachineTrayCoverLW.render(f5);
		//MachineTrayCoverRW.render(f5);
		//MachineTrayCoverTW.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
