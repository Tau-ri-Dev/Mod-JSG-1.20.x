package dev.tauri.jsg.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public class I18n {
    public static String format(String s) {
        return Component.translatable(s).getString();
    }

    public static String format(String s, Object... pArgs) {
        return Component.translatable(s, pArgs).getString();
    }

    public interface ILineFormat {
        MutableComponent apply(int lineNumber, MutableComponent component);
    }

    public static class AdvancedTooltip {
        public String key;
        public ILineFormat lineFormatting;

        public AdvancedTooltip(String key, ILineFormat lineFormatting) {
            this.key = key;
            this.lineFormatting = lineFormatting;
        }

        public int getWidth() {
            List<Component> l = formatLines();
            int textWidth = 0;
            for (Component c : l) {
                int ii = Minecraft.getInstance().font.width(c);
                if (ii > textWidth) {
                    textWidth = ii;
                }
            }
            int spaceWidth = Minecraft.getInstance().font.width(" ");
            return (int) Math.ceil((double) textWidth / (double) spaceWidth);
        }

        public List<Component> formatLines() {
            String text = format(key);
            String[] lines = text.split("%nl%");
            List<Component> linesC = new ArrayList<>();
            int i = 0;
            for (String line : lines)
                linesC.add(lineFormatting.apply(++i, Component.literal(" " + line)));
            if (lines.length > 0 && lines[0].equals(key)) return null;
            return linesC;
        }
    }


    public static AdvancedTooltip getAdvancedTooltip(String s, ILineFormat lineFormat) {
        return new AdvancedTooltip(s, lineFormat);
    }
}
