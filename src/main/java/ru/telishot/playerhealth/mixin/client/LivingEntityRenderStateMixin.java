package ru.telishot.playerhealth.mixin.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import ru.telishot.playerhealth.HealthInfo;
import ru.telishot.playerhealth.HpText;

/**
 * Хранит HP существа и кэшированную метку прямо в render state.
 * Игра сама пулит и переиспользует render states, поэтому данные
 * создавали и умирали вместе с состоянием сущности — без утечек.
 * Текст перестраивается только при изменении значений HP.
 */
@Mixin(LivingEntityRenderState.class)
public abstract class LivingEntityRenderStateMixin implements HealthInfo {

    @Unique
    private Component playerhealth$component;

    @Unique
    private float playerhealth$lastHp = Float.NaN;

    @Unique
    private float playerhealth$lastMax = Float.NaN;

    @Unique
    private float playerhealth$lastAbsorption = Float.NaN;

    @Unique
    private Vec3 playerhealth$attachment;

    @Unique
    private float playerhealth$attachmentYaw = Float.NaN;

    @Unique
    private float playerhealth$attachmentHeight = Float.NaN;

    @Override
    public boolean playerhealth$hasHealthData() {
        return playerhealth$component != null;
    }

    @Override
    public Component playerhealth$getHpComponent() {
        return playerhealth$component;
    }

    @Override
    public void playerhealth$setHealthData(float hp, float max, float absorption) {
        if (playerhealth$component != null
                && Float.compare(hp, playerhealth$lastHp) == 0
                && Float.compare(max, playerhealth$lastMax) == 0
                && Float.compare(absorption, playerhealth$lastAbsorption) == 0) {
            return;
        }

        playerhealth$component = HpText.component(hp, max, absorption);
        playerhealth$lastHp = hp;
        playerhealth$lastMax = max;
        playerhealth$lastAbsorption = absorption;
    }

    @Override
    public Vec3 playerhealth$findNameTagAttachment(EntityAttachments attachments, int point,
                                                   float yRot, float boundingBoxHeight) {
        if (Float.isNaN(playerhealth$attachmentYaw)
                || Math.abs(yRot - playerhealth$attachmentYaw) >= 0.01F
                || Float.compare(boundingBoxHeight, playerhealth$attachmentHeight) != 0
                || playerhealth$attachment == null) {
            playerhealth$attachment = attachments.getNullable(EntityAttachment.NAME_TAG, point, yRot);
            playerhealth$attachmentYaw = yRot;
            playerhealth$attachmentHeight = boundingBoxHeight;
        }
        return playerhealth$attachment;
    }
}
