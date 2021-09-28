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
     * Send a list of {@code Component}s to an {@code Audience}.
     *
     * @param audience   the audience
     * @param components the list of components
     */
    public static void sendMessages(final @NonNull Audience audience, final @NonNull List<@NonNull Component> components) {
        for (final Component c : components) {
            audience.sendMessage(c);
        }
    }

    /**
     * If {@code string} is blank or null, returns null. Else, parses a string
     * using MiniMessage and returns it.
     *
     * @param string the message to parse
     * @return {@code null}, or the component parsed by MiniMessage
     */
    public static @Nullable Component miniMessageElseNull(final @Nullable String string) {
        if (string == null || string.isBlank()) {
            return null;
        }
        return MiniMessage.miniMessage().parse(string);
    }

    /**
     * If {@code string} is blank or null, returns null. Else, parses a string
     * using MiniMessage and sends it to {@code audience}.
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
