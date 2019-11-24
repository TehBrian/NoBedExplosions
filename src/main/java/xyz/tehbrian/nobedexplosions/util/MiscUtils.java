package xyz.tehbrian.nobedexplosions.util;

import org.bukkit.ChatColor;

public class MiscUtils {

    private MiscUtils() {
    }

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}