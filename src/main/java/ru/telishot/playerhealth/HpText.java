package ru.telishot.playerhealth;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

/**
 * Формирование текста HP. Чистые функции без состояния и без аллокаций
 * промежуточных объектов, кроме итоговой строки и Component.
 */
public final class HpText {

    private static final Style HEART_STYLE = Style.EMPTY.withFont(new FontDescription.Resource(
            Identifier.fromNamespaceAndPath("playerhealth", "heart")));

    private HpText() {
    }

    /**
     * Собирает метку в виде ванильного сердечка и точного значения HP
     * (включая поглощение от золотых яблок). Число всегда красное и не
     * зависит от доли здоровья; сердечко рисуется глифом собственного
     * шрифта {@code playerhealth:heart} в стиле ванильного HUD-сердца.
     */
    public static MutableComponent component(float hp, float max, float absorption) {
        float shown = hp + absorption;
        return Component.literal("\uE000").withStyle(HEART_STYLE)
                .withStyle(ChatFormatting.RED)
                .append(Component.literal(" " + format(shown)).withStyle(ChatFormatting.RED));
    }

    private static String format(float value) {
        return Integer.toString((int) Math.ceil(value - 1.0e-4f));
    }
}
