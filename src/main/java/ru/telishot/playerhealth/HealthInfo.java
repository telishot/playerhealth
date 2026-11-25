package ru.telishot.playerhealth;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.phys.Vec3;

/**
 * Duck-интерфейс на {@code LivingEntityRenderState}: данные HP живут прямо
 * в render state игры, который сам пулит и переиспользует состояния, поэтому
 * никаких собственных Map/кэшей с ключами-ссылками не нужно и утечки невозможны.
 */
public interface HealthInfo {
    boolean playerhealth$hasHealthData();

    Component playerhealth$getHpComponent();

    void playerhealth$setHealthData(float hp, float max, float absorption);

    /**
     * Точка крепления таблички с кэшем: пересчитывается только при смене
     * поворота или высоты существа, а не каждый кадр.
     */
    Vec3 playerhealth$findNameTagAttachment(EntityAttachments attachments, int point,
                                            float yRot, float boundingBoxHeight);
}
