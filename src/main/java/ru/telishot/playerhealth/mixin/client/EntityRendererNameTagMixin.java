package ru.telishot.playerhealth.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyArgs;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.wrapper.Args;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.telishot.playerhealth.HealthInfo;

/**
 * Рисует строку HP под обычной табличкой с ником игрока.
 *
 * <p>Перехватывается ванильный вызов {@code SubmitNodeCollector#submitNameTag}
 * внутри {@code EntityRenderer#submitNameDisplay}: ванильное позиционирование,
 * дистанция, невидимость, скрадывание и подсветка остаются родными, мы лишь
 * отправляем вторую строку со сдвигом вниз.
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererNameTagMixin {

    @ModifyArgs(
            method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitNameTag(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/level/CameraRenderState;)V"
            )
    )
    private void playerhealth$submitHpTag(
            Args args,
            @Local(argsOnly = true) EntityRenderState state,
            @Local(argsOnly = true) SubmitNodeCollector collector
    ) {
        if (!(state instanceof HealthInfo info) || !info.playerhealth$hasHealthData()) {
            return;
        }

        PoseStack poseStack = args.get(0);
        Vec3 attachment = args.get(1);
        int offset = args.get(2);
        boolean seeThrough = args.get(4);
        int background = args.get(5);
        double light = args.get(6);
        CameraRenderState camera = args.get(7);
        Component hpComponent = info.playerhealth$getHpComponent();

        poseStack.pushPose();
        poseStack.translate(0.0F, -0.30F, 0.0F);
        collector.submitNameTag(poseStack, attachment, offset, hpComponent, seeThrough, background, light, camera);
        poseStack.popPose();
    }
}
