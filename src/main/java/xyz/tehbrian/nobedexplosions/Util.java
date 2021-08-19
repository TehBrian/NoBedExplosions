package xyz.tehbrian.nobedexplosions;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;


public final class Util {

    private Util() {

    }

    /**
     * Send a list of components to an audience.
     *
     * @param audience      the audience
     * @param componentList the list of components
     */
    public static void sendMessages(final @NonNull Audience audience, final @NonNull List<@NonNull Component> componentList) {
        for (final Component c : componentList) {
            audience.sendMessage(c);
        }
    }

    /**
     * Parses a string using MiniMessage or, if the string is blank or null, returns null.
     *
     * @param string the message to parse
     * @return the component parsed by MiniMessage
     */
    public static @Nullable Component miniMessageElseNull(final @Nullable String string) {
        if (string == null || string.isBlank()) {
            return null;
        }
        return MiniMessage.get().parse(string);
    }

    /**
     * Parses a string using MiniMessage or, if the string is blank or null, returns null, and sends it
     * if it isn't null.
     *
     * @param audience who to send the message to
     * @param string   the message to parse and send
     */
    public static void sendMessageOrIgnore(final @NonNull Audience audience, final @Nullable String string) {
        final @Nullable Component message = miniMessageElseNull(string);
        if (message != null) {
            audience.sendMessage(message);
        }
    }

}
