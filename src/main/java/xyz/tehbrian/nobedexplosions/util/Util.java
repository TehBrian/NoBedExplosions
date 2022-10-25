package xyz.tehbrian.nobedexplosions.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Util {

  private Util() {
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
    return MiniMessage.miniMessage().deserialize(string);
  }

  /**
   * If {@code string} is blank or null, does nothing. Else, parses a string
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
