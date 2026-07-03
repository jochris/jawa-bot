// SPDX-License-Identifier: GPL-3.0-or-later
package bot;

/**
 * Bot configuration constants.
 * Modify these values to customize your bot.
 */
public final class Config {

    private Config() {}

    /** Bot display name shown in presence. */
    public static final String BOT_NAME = "JaWa Bot";

    /** Command prefix (e.g. ".menu", ".ping"). */
    public static final String PREFIX = ".";

    /** Bot version string. */
    public static final String VERSION = "1.0.0";

    /** Startup timestamp for uptime calculation. */
    public static final long START_TIME = System.currentTimeMillis();

    /** Default session file path. Override with -Dbot.session=path */
    public static String sessionPath() {
        String prop = System.getProperty("bot.session");
        return (prop != null && !prop.isBlank() && !"null".equals(prop))
                ? prop : "sessions/bot.session";
    }
}
