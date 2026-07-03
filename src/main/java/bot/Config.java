// SPDX-License-Identifier: MIT
package bot;

public final class Config {

    private Config() {}

    public static final String BOT_NAME = "JaWa Bot";
    public static final String PREFIX = ".";
    public static final String VERSION = "1.0.0";
    public static final boolean SELF = true;
    public static final String OWNER_NUMBER = "";
    public static final long START_TIME = System.currentTimeMillis();

    public static String sessionPath() {
        String prop = System.getProperty("bot.session");
        return (prop != null && !prop.isBlank() && !"null".equals(prop))
                ? prop : "sessions/bot.session";
    }
}
