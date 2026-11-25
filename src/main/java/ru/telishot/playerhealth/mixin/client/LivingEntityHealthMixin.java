package ru.telishot.playerhealth.mixin.client;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.telishot.playerhealth.HealthInfo;

/**
 * Извлекает HP любого живого существа (игроки и мобы) в render state на этапе extraction.
 *
 * <p>Ваниль присваивает {@code state.nameTag} только существам с именем, поэтому
 * для безымянных мобов текст HP становится самой табличкой: сердечки появляются
 * над всеми мобами в радиусе ванильных правил (64 блока, команды, невидимость),
 * без второго прохода рендера и без собственных карт сущностей.
 */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityHealthMixin {

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At("TAIL")
    )
    private void playerhealth$extractHealth(LivingEntity entity, LivingEntityRenderState state,
                                            float partialTicks, CallbackInfo ci) {
        if (!(state instanceof HealthInfo info)
                || entity.isRemoved()
                || entity.isSpectator()
                || entity.getMaxHealth() <= 0.0F) {
            return;
        }
        info.playerhealth$setHealthData(
                entity.getHealth(),
                entity.getMaxHealth(),
                entity.getAbsorptionAmount()
        );
        if (state.nameTag == null && state.distanceToCameraSq < 4096.0) {
            state.nameTag = info.playerhealth$getHpComponent();
            state.nameTagAttachment = entity.getAttachments()
                    .getNullable(EntityAttachment.NAME_TAG, 0, entity.getYRot(partialTicks));
        }
    }
}
