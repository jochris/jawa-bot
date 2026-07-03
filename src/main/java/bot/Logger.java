// SPDX-License-Identifier: MIT
package bot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logger {

    private Logger() {}

    public static final String RESET   = "\u001B[0m";
    public static final String RED     = "\u001B[31m";
    public static final String GREEN   = "\u001B[32m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String BLUE    = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN    = "\u001B[36m";
    public static final String WHITE   = "\u001B[37m";
    public static final String GRAY    = "\u001B[90m";
    public static final String BOLD    = "\u001B[1m";
    public static final String DIM     = "\u001B[2m";

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static void info(String message) {
        log(CYAN, "INFO", message);
    }

    public static void success(String message) {
        log(GREEN, " OK ", message);
    }

    public static void warn(String message) {
        log(YELLOW, "WARN", message);
    }

    public static void error(String message) {
        log(RED, " ERR", message);
    }

    public static void error(String message, Throwable t) {
        log(RED, " ERR", message + " → " + t.getMessage());
    }

    public static void debug(String message) {
        log(GRAY, "DBUG", message);
    }

    public static void incoming(String sender, String chat, String text) {
        String ts = timestamp();
        String arrow = GREEN + "◄──" + RESET;
        String senderFmt = CYAN + Serialize.truncateJid(sender) + RESET;
        String chatFmt = chat != null && !chat.equals(sender)
                ? MAGENTA + " @ " + Serialize.truncateJid(chat) + RESET : "";
        String textFmt = WHITE + (text != null ? text : "<interactive>") + RESET;
        System.out.printf("%s %s %s%s │ %s%n", ts, arrow, senderFmt, chatFmt, textFmt);
    }

    public static void outgoing(String target, String msgId) {
        String ts = timestamp();
        String arrow = BLUE + "──►" + RESET;
        String targetFmt = CYAN + Serialize.truncateJid(target) + RESET;
        String idFmt = GRAY + msgId + RESET;
        System.out.printf("%s %s %s │ id=%s%n", ts, arrow, targetFmt, idFmt);
    }

    private static void log(String color, String tag, String message) {
        System.out.printf("%s %s[%s]%s %s%n", timestamp(), color, tag, RESET, message);
    }

    private static String timestamp() {
        return GRAY + LocalDateTime.now().format(TIME_FMT) + RESET;
    }

    public static void banner() {
        System.out.println();
        System.out.println(CYAN + BOLD + "  ╔══════════════════════════════════╗");
        System.out.println("  ║     🤖  JaWa Bot Starter Kit     ║");
        System.out.println("  ║        v" + Config.VERSION + " • Java " + Runtime.version().feature() + "         ║");
        System.out.println("  ╚══════════════════════════════════╝" + RESET);
        System.out.println();
    }

    public static void divider() {
        System.out.println(GRAY + "  ─────────────────────────────────────" + RESET);
    }

    public static void config(String key, String value) {
        System.out.printf("  %s%-12s%s %s%s%s%n", DIM, key + ":", RESET, WHITE, value, RESET);
    }
}
