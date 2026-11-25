package ru.telishot.playerhealth.mixin.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import ru.telishot.playerhealth.HealthInfo;
import ru.telishot.playerhealth.HpText;

/**
 * Хранит HP игрока и кэшированную строку-метку прямо в render state.
 * Игра сама пулит и переиспользует render states, так что данные
 * создавались и умирали вместе с состоянием сущности — без утечек.
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

        playerhealth$component = Component.literal(
                HpText.build(hp, max, absorption)
        ).withStyle(HpText.color(hp, max, absorption));
        playerhealth$lastHp = hp;
        playerhealth$lastMax = max;
        playerhealth$lastAbsorption = absorption;
    }
}
