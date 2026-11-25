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
 * внутри {@code EntityRenderer#submitNameDisplay}: позиционирование, дистанция,
 * скрадывание и подсветка остаются родными. И ник, и строка HP принудительно
 * отправляются с {@code seeThrough = false} (ваниль по умолчанию рисует таблички
 * в see-through слое — они просвечивают сквозь стены); строка HP отправляется в
 * увеличенном масштабе ({@code seeThrough = false} — сквозь стены текст не виден).
 * Безымянным мобам HP становится самой табличкой.
 *
 * <p>Используется только чистый Mixin ({@code @Inject} + {@code @Redirect}),
 * MixinExtras не требуется. Строка HP рисуется в целочисленном масштабе 2x
 * относительно ванильных табличек: дробный масштаб размывает пиксели глифа,
 * а целочисленный даёт чёткие края. Под niком строка HP опускается на 0.55
 * (в масштабированных координатах — по высоте ванильной строки), чтобы
 * фоновые плашки табличек не накладывались друг на друга.
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererNameTagMixin {

    @Unique
    private static final float playerhealth$HP_SCALE = 2.0F;

    @Unique
    private EntityRenderState playerhealth$currentState;

    @Inject(
            method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;I)V",
            at = @At("HEAD")
    )
    private void playerhealth$captureState(EntityRenderState state, PoseStack poseStack,
                                           SubmitNodeCollector collector, CameraRenderState camera,
                                           int offset, CallbackInfo ci) {
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
                                          boolean seeThrough, int lightCoords, double distanceToCameraSq,
                                          CameraRenderState camera) {
        if (playerhealth$currentState instanceof HealthInfo info && info.playerhealth$hasHealthData()) {
            Component hpComponent = info.playerhealth$getHpComponent();
            if (name == hpComponent) {
                playerhealth$submitScaled(instance, poseStack, attachment, offset, 0.0F, hpComponent,
                        lightCoords, distanceToCameraSq, camera);
            } else if (playerhealth$currentState.nameTag == name) {
                instance.submitNameTag(poseStack, attachment, offset, name,
                        false, lightCoords, distanceToCameraSq, camera);
                playerhealth$submitScaled(instance, poseStack, attachment, offset, 0.55F, hpComponent,
                        lightCoords, distanceToCameraSq, camera);
            } else {
                instance.submitNameTag(poseStack, attachment, offset, name,
                        false, lightCoords, distanceToCameraSq, camera);
            }
            return;
        }
        instance.submitNameTag(poseStack, attachment, offset, name,
                seeThrough, lightCoords, distanceToCameraSq, camera);
    }

    /**
     * Отправляет текст HP в увеличенном масштабе. Ваниль применяется к табличке
     * смещение к точке крепления и масштаб 0.025 внутри себя, поэтому точку
     * крепления и вертикальный сдвиг компенсируем заранее: точка привязки
     * остаётся на ванильном месте, растёт только сам текст.
     */
    @Unique
    private static void playerhealth$submitScaled(SubmitNodeCollector collector, PoseStack poseStack,
                                                  Vec3 attachment, int offset, float drop, Component text,
                                                  int lightCoords, double distanceToCameraSq,
                                                  CameraRenderState camera) {
        if (attachment == null) {
            return;
        }
        float scale = playerhealth$HP_SCALE;
        float anchorX = (float) attachment.x;
        float anchorY = (float) attachment.y + 0.5F;
        float anchorZ = (float) attachment.z;
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        poseStack.translate(
                anchorX / scale - anchorX,
                (anchorY - drop) / scale - anchorY,
                anchorZ / scale - anchorZ
        );
        collector.submitNameTag(poseStack, attachment, offset, text,
                false, lightCoords, distanceToCameraSq, camera);
        poseStack.popPose();
    }
}
