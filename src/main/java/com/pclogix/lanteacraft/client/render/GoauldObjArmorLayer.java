package com.pclogix.lanteacraft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.client.model.GoauldSoldierModel;
import com.pclogix.lanteacraft.entity.GoauldSoldierEntity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoauldObjArmorLayer extends RenderLayer<GoauldSoldierEntity, GoauldSoldierModel> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "models/obj/goauld_jaffa_armor.obj");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/entity/goauld_jaffa_armor.png");
    private static final ResourceLocation HELMET_MODEL = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "models/obj/goauld_serpent_helmet.obj");
    private static final ResourceLocation HELMET_TEXTURE = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/entity/goauld_serpent_helmet.png");
    private ObjModel model;
    private ObjModel helmetModel;

    public GoauldObjArmorLayer(RenderLayerParent<GoauldSoldierEntity, GoauldSoldierModel> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, GoauldSoldierEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ObjModel armor = model();
        ObjModel helmet = helmetModel();
        if (armor == null) {
            return;
        }

        GoauldSoldierModel parentModel = getParentModel();
        poseStack.pushPose();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        applyArmorPose(poseStack);
        armor.render("bodyModel", poseStack.last(), consumer, packedLight);
        poseStack.popPose();

        renderAnimatedPart(poseStack, armor, consumer, packedLight, "leftArmModel", parentModel.leftArm, 5.0F, 2.0F, 0.0F);
        renderAnimatedPart(poseStack, armor, consumer, packedLight, "rightArmModel", parentModel.rightArm, -5.0F, 2.0F, 0.0F);
        renderAnimatedPart(poseStack, armor, consumer, packedLight, "leftLegModel", parentModel.leftLeg, 1.9F, 12.0F, 0.0F);
        renderAnimatedPart(poseStack, armor, consumer, packedLight, "leftFootModel", parentModel.leftLeg, 1.9F, 12.0F, 0.0F);
        renderAnimatedPart(poseStack, armor, consumer, packedLight, "rightLegModel", parentModel.rightLeg, -1.9F, 12.0F, 0.0F);
        renderAnimatedPart(poseStack, armor, consumer, packedLight, "rightFootModel", parentModel.rightLeg, -1.9F, 12.0F, 0.0F);

        if (helmet == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.0D, -0.48D, 0.0D);
        poseStack.scale(-0.052F, 0.052F, 0.052F);
        poseStack.translate(0.0D, 0.0D, 0.0D);
        VertexConsumer helmetConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(HELMET_TEXTURE));
        helmet.render("headModel", poseStack.last(), helmetConsumer, packedLight);
        poseStack.popPose();
    }

    private static void applyArmorPose(PoseStack poseStack) {
        poseStack.translate(0.0D, -0.42D, 0.0D);
        poseStack.scale(-0.075F, 0.075F, 0.075F);
        poseStack.translate(0.0D, 2.0D, 0.0D);
    }

    private static void renderAnimatedPart(PoseStack poseStack, ObjModel armor, VertexConsumer consumer, int packedLight, String partName, ModelPart part, float pivotX, float pivotY, float pivotZ) {
        poseStack.pushPose();
        applyArmorPose(poseStack);
        poseStack.translate(pivotX, pivotY, pivotZ);
        if (part.zRot != 0.0F) {
            poseStack.mulPose(Axis.ZP.rotation(part.zRot));
        }
        if (part.yRot != 0.0F) {
            poseStack.mulPose(Axis.YP.rotation(part.yRot));
        }
        if (part.xRot != 0.0F) {
            poseStack.mulPose(Axis.XP.rotation(part.xRot));
        }
        poseStack.translate(-pivotX, -pivotY, -pivotZ);
        armor.render(partName, poseStack.last(), consumer, packedLight);
        poseStack.popPose();
    }

    private ObjModel model() {
        if (model == null) {
            model = ObjModel.load(MODEL);
        }
        return model;
    }

    private ObjModel helmetModel() {
        if (helmetModel == null) {
            helmetModel = ObjModel.load(HELMET_MODEL);
        }
        return helmetModel;
    }

    private record ObjModel(Map<String, ObjMesh> meshes) {
        private static ObjModel load(ResourceLocation location) {
            try {
                List<double[]> positions = new ArrayList<>();
                List<double[]> uvs = new ArrayList<>();
                Map<String, List<MeshVertex[]>> triangles = new HashMap<>();
                String objectName = "default";
                triangles.put(objectName, new ArrayList<>());
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(Minecraft.getInstance().getResourceManager().open(location), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("o ") || line.startsWith("g ")) {
                            objectName = line.substring(2).trim();
                            triangles.computeIfAbsent(objectName, name -> new ArrayList<>());
                        } else if (line.startsWith("v ")) {
                            String[] parts = line.split("\\s+");
                            positions.add(new double[] { Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]) });
                        } else if (line.startsWith("vt ")) {
                            String[] parts = line.split("\\s+");
                            uvs.add(new double[] { Double.parseDouble(parts[1]), 1.0D - Double.parseDouble(parts[2]) });
                        } else if (line.startsWith("f ")) {
                            String[] parts = line.split("\\s+");
                            if (parts.length >= 4) {
                                MeshVertex first = vertex(parts[1], positions, uvs);
                                for (int i = 2; i < parts.length - 1; i++) {
                                    triangles.get(objectName).add(new MeshVertex[] { first, vertex(parts[i], positions, uvs), vertex(parts[i + 1], positions, uvs) });
                                }
                            }
                        }
                    }
                }
                Map<String, ObjMesh> meshes = new HashMap<>();
                triangles.forEach((name, meshTriangles) -> meshes.put(name, new ObjMesh(meshTriangles)));
                return new ObjModel(meshes);
            } catch (IOException | RuntimeException exception) {
                LanteaCraft.LOGGER.warn("Failed to load Goa'uld OBJ armor {}", location, exception);
                return null;
            }
        }

        private static MeshVertex vertex(String token, List<double[]> positions, List<double[]> uvs) {
            String[] indices = token.split("/");
            double[] position = positions.get(Integer.parseInt(indices[0]) - 1);
            double[] uv = indices.length > 1 && !indices[1].isBlank() ? uvs.get(Integer.parseInt(indices[1]) - 1) : new double[] { 0.0D, 0.0D };
            return new MeshVertex(position[0], position[1], position[2], uv[0], uv[1]);
        }

        private void render(String name, PoseStack.Pose pose, VertexConsumer consumer, int packedLight) {
            ObjMesh mesh = meshes.get(name);
            if (mesh != null) {
                mesh.render(pose, consumer, packedLight);
            }
        }
    }

    private record ObjMesh(List<MeshVertex[]> triangles) {
        private void render(PoseStack.Pose pose, VertexConsumer consumer, int packedLight) {
            for (MeshVertex[] triangle : triangles) {
                emitTriangle(pose, consumer, packedLight, triangle[0], triangle[1], triangle[2]);
                emitTriangle(pose, consumer, packedLight, triangle[2], triangle[1], triangle[0]);
            }
        }

        private void emitTriangle(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, MeshVertex first, MeshVertex second, MeshVertex third) {
            emitVertex(pose, consumer, packedLight, first);
            emitVertex(pose, consumer, packedLight, second);
            emitVertex(pose, consumer, packedLight, third);
        }

        private void emitVertex(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, MeshVertex vertex) {
            consumer.addVertex(pose, (float)vertex.x, (float)vertex.y, (float)vertex.z)
                    .setColor(1.0F, 1.0F, 1.0F, 1.0F)
                    .setUv((float)vertex.u, (float)vertex.v)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(packedLight)
                    .setNormal(0.0F, 1.0F, 0.0F);
        }
    }

    private record MeshVertex(double x, double y, double z, double u, double v) {
    }
}
