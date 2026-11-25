package ru.telishot.playerhealth;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Формирование текста HP. Чистые функции без состояния и без аллокаций
 * промежуточных объектов, кроме итоговой строки и Component.
 */
public final class HpText {

    private static final String HEART = "\u2665";

    private HpText() {
    }

    /**
     * Собирает метку вида "♥ 12": красное сердце и точное значение HP
     * (включая поглощение от золотых яблок), число окрашивается по доле здоровья.
     */
    public static MutableComponent component(float hp, float max, float absorption) {
        float shown = hp + absorption;
        return Component.literal(HEART).withStyle(ChatFormatting.RED)
                .append(Component.literal(" " + format(shown)).withStyle(color(hp, max, absorption)));
    }

    private static String format(float value) {
        return Integer.toString((int) Math.ceil(value - 1.0e-4f));
    }

    /**
     * Цвет числа в зависимости от доли здоровья: зелёный → жёлтый → красный.
     */
    public static ChatFormatting color(float hp, float max, float absorption) {
        float fraction = (hp + absorption) / Math.max(1.0f, max);
        if (fraction > 0.5f) {
            return ChatFormatting.GREEN;
        }
        if (fraction > 0.25f) {
            return ChatFormatting.YELLOW;
        }
        return ChatFormatting.RED;
    }
}
