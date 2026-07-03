// SPDX-License-Identifier: MIT
package bot;

public final class Serialize {

    private Serialize() {}

    public static String bold(String text) {
        return "*" + text + "*";
    }

    public static String italic(String text) {
        return "_" + text + "_";
    }

    public static String mono(String text) {
        return "```\n" + text + "\n```";
    }

    public static String code(String text) {
        return "`" + text + "`";
    }

    public static String strike(String text) {
        return "~" + text + "~";
    }

    public static String formatUptime(long millis) {
        long seconds = millis / 1000;
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        var sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        sb.append(secs).append("s");
        return sb.toString();
    }

    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        double kb = bytes / 1024.0;
        if (kb < 1024) return String.format("%.1f KB", kb);
        double mb = kb / 1024.0;
        if (mb < 1024) return String.format("%.1f MB", mb);
        double gb = mb / 1024.0;
        return String.format("%.1f GB", gb);
    }

    public static String truncateJid(String jid) {
        if (jid == null) return "?";
        int at = jid.indexOf('@');
        String user = at > 0 ? jid.substring(0, at) : jid;
        String server = at > 0 ? jid.substring(at) : "";
        if (user.length() > 10) {
            user = user.substring(0, 6) + ".." + user.substring(user.length() - 4);
        }
        if (server.contains("g.us")) return user + "@group";
        if (server.contains("lid")) return user + "@lid";
        return user;
    }

    public static String field(String label, String value) {
        return "• " + bold(label) + ": " + value;
    }
}
