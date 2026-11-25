package ru.telishot.playerhealth;

import net.minecraft.network.chat.Component;

/**
 * Duck-интерфейс на {@code LivingEntityRenderState}: данные HP живут прямо
 * в render state игры, который переиспользуется пулом, поэтому никаких
 * собственных Map/кэшей с ключами-ссылками не нужно и утечки невозможны.
 */
public interface HealthInfo {
    boolean playerhealth$hasHealthData();

    Component playerhealth$getHpComponent();

    void playerhealth$setHealthData(float hp, float max, float absorption);
}
