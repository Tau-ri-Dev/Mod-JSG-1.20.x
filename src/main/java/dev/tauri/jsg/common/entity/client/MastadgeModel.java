package dev.tauri.jsg.common.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tauri.jsg.common.entity.animal.MastadgeEntity;
import dev.tauri.jsg.common.entity.animations.MastadgeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class MastadgeModel<T extends Entity> extends HierarchicalModel<T> {
    private final ModelPart mastadge;
    private final ModelPart legs;
    private final ModelPart front_legs;
    private final ModelPart right_leg_f;
    private final ModelPart left_leg_f;
    private final ModelPart back_legs;
    private final ModelPart left_leg_b;
    private final ModelPart right_leg_b;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart hitbox;

    private final List<ModelPart> saddleParts = new ArrayList<>();
    private final List<ModelPart> ridingParts = new ArrayList<>();

    public MastadgeModel(ModelPart root) {
        this.mastadge = root.getChild("model");
        this.legs = mastadge.getChild("legs");
        this.front_legs = legs.getChild("front_legs");
        this.right_leg_f = front_legs.getChild("right_leg_f");
        this.left_leg_f = front_legs.getChild("left_leg_f");
        this.back_legs = legs.getChild("back_legs");
        this.left_leg_b = back_legs.getChild("left_leg_b");
        this.right_leg_b = back_legs.getChild("right_leg_b");
        this.body = mastadge.getChild("body");
        this.tail = body.getChild("tail");
        this.neck = mastadge.getChild("neck");
        this.head = neck.getChild("head");
        this.jaw = head.getChild("jaw");
        this.hitbox = root.getChild("hitbox");

        this.saddleParts.add(body.getChild("mount_r1"));
        this.ridingParts.add(head.getChild("acces_r1"));
        this.ridingParts.add(head.getChild("acces_r2"));
        this.ridingParts.add(head.getChild("acces_r3"));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition model = partdefinition.addOrReplaceChild("model", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 15.0F));

        PartDefinition legs = model.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0667F, -3.9687F));

        PartDefinition front_legs = legs.addOrReplaceChild("front_legs", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, -13.5F));

        front_legs.addOrReplaceChild("right_leg_f", CubeListBuilder.create().texOffs(36, 95).addBox(-3.5F, -4.0F, -4.5F, 7.0F, 38.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(0, 197).addBox(-3.5F, -4.7F, -4.5F, 7.0F, 35.0F, 9.0F, new CubeDeformation(0.5F)), PartPose.offset(13.5F, -1.0F, 0.0F));

        front_legs.addOrReplaceChild("left_leg_f", CubeListBuilder.create().texOffs(0, 197).mirror().addBox(-3.5F, -4.7F, -4.5F, 7.0F, 36.0F, 9.0F, new CubeDeformation(0.5F)).mirror(false)
                .texOffs(1, 95).addBox(-3.5F, -4.0F, -4.5F, 7.0F, 38.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-13.5F, -1.0F, 0.0F));

        PartDefinition back_legs = legs.addOrReplaceChild("back_legs", CubeListBuilder.create(), PartPose.offset(0.0F, 7.0F, 16.5F));
        back_legs.addOrReplaceChild("left_leg_b", CubeListBuilder.create().texOffs(33, 198).mirror().addBox(-3.5F, -2.2F, -4.0F, 7.0F, 24.0F, 8.0F, new CubeDeformation(0.5F)).mirror(false)
                .texOffs(135, 1).addBox(-3.5F, -2.0F, -4.0F, 7.0F, 26.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-12.5F, -1.0F, -0.5F));
        back_legs.addOrReplaceChild("right_leg_b", CubeListBuilder.create().texOffs(62, 137).addBox(-3.5F, -2.0F, -4.0F, 7.0F, 26.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(33, 198).addBox(-3.5F, -2.2F, -4.0F, 7.0F, 24.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(12.5F, -1.0F, -0.5F));

        PartDefinition body = model.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-11.5F, -14.7594F, -25.4511F, 23.0F, 22.0F, 27.0F, new CubeDeformation(0.0F))
                .texOffs(171, 64).addBox(-11.5F, 3.9406F, -25.4511F, 23.0F, 0.0F, 27.0F, new CubeDeformation(0.0F))
                .texOffs(172, 0).mirror().addBox(-9.0F, 3.2813F, -23.1854F, 18.0F, 6.0F, 24.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(85, 206).addBox(-5.05F, 3.4319F, -22.1379F, 10.0F, 9.0F, 16.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.974F, -0.703F));

        body.addOrReplaceChild("mount_r1", CubeListBuilder.create().texOffs(186, 228).addBox(-9.15F, -6.0F, -7.7F, 18.3F, 12.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -16.9524F, -20.5479F, 0.3927F, 0.0F, 0.0F));
        body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(192, 33).mirror().addBox(-8.5F, -2.5F, -6.3F, 16.0F, 11.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.5F, 8.2813F, 7.8146F, -0.3927F, 0.0F, 0.0F));
        body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(85, 33).addBox(-8.0F, -16.5F, -11.5F, 16.0F, 20.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.8694F, -14.9116F, 0.3927F, 0.0F, 0.0F));
        body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(73, 73).addBox(-9.0F, -7.5F, -9.0F, 18.0F, 16.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -14.2594F, -13.4511F, -0.7854F, 0.0F, 0.0F));
        body.addOrReplaceChild("low2_r1", CubeListBuilder.create().texOffs(175, 91).addBox(-10.5F, 10.0F, -13.0F, 21.0F, 0.0F, 26.0F, new CubeDeformation(0.0F))
                .texOffs(0, 49).addBox(-10.5F, -7.0F, -12.9F, 21.0F, 20.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.3111F, 9.8847F, -0.3927F, 0.0F, 0.0F));
        body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -2.0F, -0.5F, 5.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.3111F, 22.3847F));

        PartDefinition neck = model.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(106, 168).addBox(-4.65F, -6.95F, -14.2F, 10.0F, 14.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 143).addBox(-5.0F, 6.626F, -11.6919F, 10.0F, 12.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.6015F, -22.59F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(134, 36).addBox(-4.6F, 2.4361F, -12.774F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.3F))
                .texOffs(70, 111).addBox(-7.0F, -9.4139F, -12.224F, 14.0F, 13.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(156, 85).addBox(7.1F, -11.0139F, -12.224F, 0.0F, 6.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(156, 85).mirror().addBox(-7.1F, -11.0139F, -12.224F, 0.0F, 9.0F, 11.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 1.79F, -9.2179F));

        head.addOrReplaceChild("acces_r1", CubeListBuilder.create().texOffs(34, 172).addBox(-6.95F, -7.35F, -1.0F, 14.0F, 14.0F, 2.0F, new CubeDeformation(0.4F)), PartPose.offsetAndRotation(-0.05F, -1.9381F, -10.9249F, 0.5236F, 0.0F, 0.0F));
        head.addOrReplaceChild("acces_r2", CubeListBuilder.create().texOffs(151, 62).addBox(-9.5F, 2.3F, -3.6F, 18.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 3.5828F, -3.1083F, -0.7854F, 0.0F, 0.0F));
        head.addOrReplaceChild("acces_r3", CubeListBuilder.create().texOffs(167, 131).mirror().addBox(-1.7F, -7.7F, -5.2F, 17.0F, 12.0F, 22.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-6.8F, -2.4172F, -5.1083F, 0.7854F, 0.0F, 0.0F));
        head.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(110, 0).addBox(-5.6F, -2.35F, -3.0F, 10.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 5.4361F, -14.774F, 0.3927F, 0.0F, 0.0F));
        head.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(121, 111).addBox(-3.6F, -7.0F, -4.2F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.4F, 0.0787F, -16.7418F, 0.3927F, 0.0F, 0.0F));
        head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(74, 0).addBox(-5.0F, 1.1F, -14.4F, 10.0F, 9.0F, 16.0F, new CubeDeformation(0.1F))
                .texOffs(150, 49).addBox(-5.5F, -1.05F, -14.45F, 11.0F, 2.0F, 6.0F, new CubeDeformation(0.25F))
                .texOffs(140, 136).addBox(-6.0F, -1.45F, -8.55F, 12.0F, 3.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(132, 70).addBox(-4.5F, -3.6F, -13.2F, 9.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0361F, -3.474F));

        partdefinition.addOrReplaceChild("hitbox", CubeListBuilder.create().texOffs(62, 145).addBox(-16.0F, -6.0F, -32.5F, 32.0F, 46.0F, 65.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -16.0F, 0.5F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        var pEntity = (MastadgeEntity) entity;

        boolean isSaddled = pEntity.isSaddled();
        boolean isRidden = pEntity.isVehicle();

        for (ModelPart s : this.saddleParts) {
            s.visible = isSaddled;
        }

        for (ModelPart r : this.ridingParts) {
            r.visible = isRidden && isSaddled;
        }

        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch, ageInTicks);

        this.animateWalk(MastadgeAnimations.WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.animate(pEntity.sitPoseAnimationState, MastadgeAnimations.SIT, ageInTicks, 1.0F);
        this.animate(pEntity.sitUpAnimationState, MastadgeAnimations.SIT_UP, ageInTicks, 1.0F);
        this.animate(pEntity.idleAnimationState, MastadgeAnimations.IDLE, ageInTicks, 1.0F);
        this.animate(pEntity.dashAnimationState, MastadgeAnimations.EAT, ageInTicks, 1.0F);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch, float pAgeInTicks) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

        this.head.yRot = pNetHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float) Math.PI / 180F);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        mastadge.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public @NotNull ModelPart root() {
        return mastadge;
    }
}
