package ru.telishot.playerhealth.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.telishot.playerhealth.HealthInfo;

/**
 * Рисует строку HP под обычной табличкой с ником игрока.
 *
 * <p>Перехватывается ванильный вызов {@code SubmitNodeCollector#submitNameTag}
 * внутри {@code EntityRenderer#submitNameDisplay}: ванильное позиционирование,
 * дистанция, невидимость, скрадывание и подсветка остаются родными, мы лишь
 * отправляем вторую строку со сдвигом вниз.
 *
 * <p>Используется только чистый Mixin ({@code @Inject} + {@code @Redirect}),
 * MixinExtras не требуется.
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererNameTagMixin {

    @Unique
    private EntityRenderState playerhealth$currentState;

    @Inject(
            method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;I)V",
            at = @At("HEAD")
    )
    private void playerhealth$captureState(EntityRenderState state, PoseStack poseStack,
                                           SubmitNodeCollector collector, CameraRenderState camera,
                                           int light, CallbackInfo ci) {
        playerhealth$currentState = state;
    }

    @Redirect(
            method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitNameTag(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/level/CameraRenderState;)V"
            )
    )
    private void playerhealth$submitHpTag(SubmitNodeCollector instance, PoseStack poseStack,
                                          Vec3 attachment, int offset, Component name,
                                          boolean seeThrough, int background, double light,
                                          CameraRenderState camera) {
        instance.submitNameTag(poseStack, attachment, offset, name, seeThrough, background, light, camera);

        if (playerhealth$currentState instanceof HealthInfo info
                && info.playerhealth$hasHealthData()) {
            poseStack.pushPose();
            poseStack.translate(0.0F, -0.30F, 0.0F);
            instance.submitNameTag(poseStack, attachment, offset,
                    info.playerhealth$getHpComponent(), seeThrough, background, light, camera);
            poseStack.popPose();
        }
    }
}
