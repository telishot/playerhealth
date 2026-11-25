package ru.telishot.playerhealth;

import net.minecraft.ChatFormatting;

/**
 * Формирование текста HP. Чистые функции без состояния и без аллокаций
 * промежуточных объектов, кроме итоговой строки.
 */
public final class HpText {

    private HpText() {
    }

    /**
     * Собирает строку вида "8/20": точное значение HP (включая поглощение
     * от золотых яблок) и максимальный запас.
     */
    public static String build(float hp, float max, float absorption) {
        float shown = hp + absorption;
        StringBuilder sb = new StringBuilder(12);
        sb.append(format(shown)).append('/').append(format(max));
        return sb.toString();
    }

    private static String format(float value) {
        return Integer.toString((int) Math.ceil(value - 1.0e-4f));
    }

    /**
     * Цвет текста в зависимости от доли здоровья: зелёный → жёлтый → красный.
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
