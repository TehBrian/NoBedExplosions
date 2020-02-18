package xyz.tehbrian.nobedexplosions.util;

import org.bukkit.ChatColor;

public class MessageUtils {

    private MessageUtils() {
    }

    public static String color(String string) {
        return string == null ? null : ChatColor.translateAlternateColorCodes('&', string);
    }
}
