package ru.telishot.playerhealth;

import net.minecraft.ChatFormatting;

/**
 * Формирование текста HP. Чистые функции без состояния и без аллокаций
 * промежуточных объектов, кроме итоговой строки.
 */
public final class HpText {
    private static final char FULL_HEART = '♥';
    private static final char EMPTY_HEART = '♡';

    private HpText() {
    }

    /**
     * Собирает строку вида "♥♥♥♡♡ 8/20": сердца + точное значение HP
     * (включая поглощение от золотых яблок).
     */
    public static String build(float hp, float max, float absorption) {
        float shown = hp + absorption;
        int maxHearts = Math.max(1, Math.round(max / 2.0f));
        float perHeart = max / maxHearts;

        int full = (int) Math.min(maxHearts, Math.max(0, shown / perHeart));
        boolean half = shown - full * perHeart > 0.01f && full < maxHearts;
        int hearts = Math.min(maxHearts, 10);

        StringBuilder sb = new StringBuilder(hearts + 12);
        for (int i = 0; i < hearts; i++) {
            if (i < full) {
                sb.append(FULL_HEART);
            } else if (i == full && half) {
                sb.append(FULL_HEART);
            } else {
                sb.append(EMPTY_HEART);
            }
        }
        sb.append(' ');
        sb.append((int) shown).append('/').append((int) max);
        return sb.toString();
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
