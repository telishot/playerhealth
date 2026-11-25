package ru.telishot.playerhealth.mixin.client;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.telishot.playerhealth.HealthInfo;

/**
 * Извлекает HP любого живого существа (игроки и мобы) в render state один раз
 * за кадр (на этапе extraction), а не на пути рендера отрисовки.
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
                || entity.isSpectator()) {
            return;
        }
        info.playerhealth$setHealthData(
                entity.getHealth(),
                entity.getMaxHealth(),
                entity.getAbsorptionAmount()
        );
    }
}
