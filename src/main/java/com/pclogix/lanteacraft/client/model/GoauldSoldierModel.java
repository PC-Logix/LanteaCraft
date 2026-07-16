package com.pclogix.lanteacraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pclogix.lanteacraft.entity.GoauldSoldierEntity;
import com.pclogix.lanteacraft.registry.ModItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoauldSoldierModel extends HumanoidModel<GoauldSoldierEntity> {
    private boolean holdingStaff;

    public GoauldSoldierModel(ModelPart root) {
        super(root);
        head.visible = false;
        hat.visible = false;
        body.visible = false;
        rightArm.visible = false;
        leftArm.visible = false;
        rightLeg.visible = false;
        leftLeg.visible = false;
    }

    @Override
    public void setupAnim(GoauldSoldierEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        holdingStaff = entity.getMainHandItem().is(ModItems.STAFF_WEAPON.get());
        rightArmPose = ArmPose.EMPTY;
        leftArmPose = ArmPose.EMPTY;
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        super.translateToHand(arm, poseStack);
        if (holdingStaff && arm == HumanoidArm.RIGHT) {
            poseStack.translate(-0.35D, 0.0D, 0.0D);
            poseStack.mulPose(Axis.ZP.rotationDegrees(-22.5F));
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                        .texOffs(0, 32)
                        .addBox(-4.6F, -8.8F, -4.6F, 9.2F, 2.0F, 9.2F)
                        .texOffs(38, 32)
                        .addBox(-5.0F, -6.9F, -4.9F, 10.0F, 2.0F, 2.0F)
                        .texOffs(64, 32)
                        .addBox(-4.5F, -6.8F, 3.6F, 9.0F, 7.0F, 1.8F),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(16, 16)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F)
                        .texOffs(0, 48)
                        .addBox(-5.0F, 1.0F, -3.0F, 10.0F, 8.0F, 2.0F)
                        .texOffs(28, 48)
                        .addBox(-4.8F, 9.0F, -2.7F, 9.6F, 2.0F, 5.4F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(40, 16)
                        .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F)
                        .texOffs(0, 62)
                        .addBox(-4.5F, -3.0F, -2.8F, 5.5F, 3.5F, 5.6F),
                PartPose.offset(-5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(32, 64)
                        .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F)
                        .texOffs(24, 62)
                        .addBox(-1.0F, -3.0F, -2.8F, 5.5F, 3.5F, 5.6F),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F)
                        .texOffs(56, 62)
                        .addBox(-2.4F, 6.0F, -2.4F, 4.8F, 5.5F, 4.8F),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                        .texOffs(48, 64)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F)
                        .texOffs(76, 62)
                        .addBox(-2.4F, 6.0F, -2.4F, 4.8F, 5.5F, 4.8F),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }
}
