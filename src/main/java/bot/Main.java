// SPDX-License-Identifier: MIT
package bot;

import id.jawa.core.JaWaClient;

public final class Main {

    public static void main(String[] args) {
        Logger.banner();

        String sessionPath = Config.sessionPath();
        Logger.config("Session", sessionPath);
        Logger.config("Prefix", Config.PREFIX);
        Logger.config("Bot Name", Config.BOT_NAME);
        Logger.divider();

        try {
            java.nio.file.Path sessionFile = java.nio.file.Path.of(sessionPath);
            String suffix = ".session";
            String basePath = sessionFile.toString();
            String derivedSignalDir = basePath.endsWith(suffix)
                ? basePath.substring(0, basePath.length() - suffix.length()) + ".signal"
                : basePath + ".signal";
            java.nio.file.Path signalDir = java.nio.file.Path.of(derivedSignalDir);

            id.jawa.store.FileAuthStore store = new id.jawa.store.FileAuthStore(sessionFile);
            var client = new JaWaClient(store, signalDir).autoReconnect(true);
            var bot = new BotClient(client);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                Logger.warn("Shutting down...");
                client.close();
            }));

            Logger.info("Connecting to WhatsApp...");
            bot.start();

            Thread.currentThread().join();

        } catch (Exception e) {
            Logger.error("Fatal error", e);
            System.exit(1);
        }
    }
}
