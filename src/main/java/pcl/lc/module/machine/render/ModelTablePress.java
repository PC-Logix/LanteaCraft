package pcl.lc.module.machine.render;

import pcl.lc.api.internal.HookedModelBase;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class ModelTablePress extends HookedModelBase {
	ModelRenderer ModelBase;
	ModelRenderer ModelSwingArm;
	ModelRenderer ModelPressJL;
	ModelRenderer ModelPressJR;
	ModelRenderer ModelSwingL;
	ModelRenderer ModelSwingR;
	ModelRenderer ModelSwingH;
	
	float hx;
	float vp;

	public ModelTablePress() {
		textureWidth = 64;
		textureHeight = 64;

		ModelBase = new ModelRenderer(this, 0, 0);
		ModelBase.addBox(-8F, -1F, -8F, 16, 2, 16);
		ModelBase.setRotationPoint(0F, 23F, 0F);
		ModelBase.setTextureSize(64, 64);
		ModelBase.mirror = true;
		setRotation(ModelBase, 0F, 0F, 0F);
		ModelSwingArm = new ModelRenderer(this, 0, 18);
		ModelSwingArm.addBox(-6F, -1F, -6F, 12, 2, 12);
		ModelSwingArm.setRotationPoint(0F, 18F, 0F);
		ModelSwingArm.setTextureSize(64, 64);
		ModelSwingArm.mirror = true;
		setRotation(ModelSwingArm, 0F, 0F, 0F);
		ModelPressJL = new ModelRenderer(this, 0, 32);
		ModelPressJL.addBox(-1F, -1F, -1F, 2, 2, 2);
		ModelPressJL.setRotationPoint(-7F, 21F, 7F);
		ModelPressJL.setTextureSize(64, 64);
		ModelPressJL.mirror = true;
		setRotation(ModelPressJL, 0F, 0F, 0F);
		ModelPressJR = new ModelRenderer(this, 0, 32);
		ModelPressJR.addBox(-1F, -1F, -1F, 2, 2, 2);
		ModelPressJR.setRotationPoint(7F, 21F, 7F);
		ModelPressJR.setTextureSize(64, 64);
		ModelPressJR.mirror = true;
		setRotation(ModelPressJR, 0F, 0F, 0F);
		ModelSwingL = new ModelRenderer(this, 0, 36);
		ModelSwingL.addBox(-0.5F, -14F, -0.5F, 1, 14, 1);
		ModelSwingL.setRotationPoint(-6.5F, 21F, 7F);
		ModelSwingL.setTextureSize(64, 64);
		ModelSwingL.mirror = true;
		setRotation(ModelSwingL, 1.103998F, 0F, 0F);
		ModelSwingR = new ModelRenderer(this, 4, 36);
		ModelSwingR.addBox(-0.5F, -14F, -0.5F, 1, 14, 1);
		ModelSwingR.setRotationPoint(6.5F, 21F, 7F);
		ModelSwingR.setTextureSize(64, 64);
		ModelSwingR.mirror = true;
		setRotation(ModelSwingR, 1.103998F, 0F, 0F);
		ModelSwingH = new ModelRenderer(this, 8, 32);
		ModelSwingH.addBox(-6F, -14F, -0.5F, 12, 1, 1);
		ModelSwingH.setRotationPoint(0F, 21F, 7F);
		ModelSwingH.setTextureSize(64, 64);
		ModelSwingH.mirror = true;
		setRotation(ModelSwingH, 1.103991F, 0F, 0F);
	}

	public void render(float f5) {
		ModelBase.render(f5);
		if (hx > 0.35f) {
			hx = 0.35f;
			vp = -0.05f;
		} else if (-0.2f > hx) {
			hx = -0.2f;
			vp = 0.05f;
		}
		if (vp == 0.0f) 
			vp = 0.01f;
		hx += vp;
		
		float dv = (float) Math.atan2(0.4f, 0.2f + hx);
		ModelSwingArm.offsetY = -hx;
		ModelSwingL.rotateAngleX = ModelSwingR.rotateAngleX = ModelSwingH.rotateAngleX = dv;
		ModelSwingArm.render(f5);
		ModelPressJL.render(f5);
		ModelPressJR.render(f5);
		ModelSwingL.render(f5);
		ModelSwingR.render(f5);
		ModelSwingH.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void preInventory(Block block, int meta, int model, RenderBlocks rbx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postInventory(Block block, int meta, int model, RenderBlocks rbx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preWorld(IBlockAccess world, int x, int y, int z, Block block, int renderId, RenderBlocks rbx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postWorld(IBlockAccess world, int x, int y, int z, Block block, int renderId, RenderBlocks rbx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preTile(TileEntity tile, double x, double y, double z, float scale) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postTile(TileEntity tile, double x, double y, double z, float scale) {
		// TODO Auto-generated method stub
		
	}

}
