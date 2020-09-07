package xyz.tehbrian.nobedexplosions.util;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    public static final Pattern HEX_PATTERN = Pattern.compile("&(#[A-Fa-f0-9]{6})");

    private MessageUtils() {
    }

    public static String color(String string) {
        return string == null ? null : replaceHex(ChatColor.translateAlternateColorCodes('&', string));
    }

    public static String replaceHex(String str) {
        Matcher matcher = HEX_PATTERN.matcher(str);
        while (matcher.find()) {
            str = str.replace(matcher.group(0), ChatColor.of(matcher.group(1)).toString());
        }
        return str;
    }
}
