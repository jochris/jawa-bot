// SPDX-License-Identifier: GPL-3.0-or-later
package bot;

import bot.handler.*;
import bot.router.Context;
import bot.router.Router;
import id.jawa.core.JaWaClient;
import id.jawa.message.MessageReceiver;

import java.util.List;

/**
 * Bot lifecycle manager. Connects to WhatsApp via JaWa, registers
 * command handlers, and routes incoming messages.
 */
public final class BotClient {

    private final JaWaClient client;
    private final Router router;

    public BotClient(JaWaClient client) {
        this.client = client;
        this.router = new Router();
        registerHandlers();
    }

    /** Register all command and interactive handlers. */
    private void registerHandlers() {
        var helpHandler = new HelpHandler(router);

        // ── Text Commands ────────────────────────────────────────────────
        router.register(Config.PREFIX + "menu",  "Tampilkan menu bot",       "General", new MenuHandler());
        router.register(Config.PREFIX + "ping",  "Cek apakah bot aktif",     "General", new PingHandler());
        router.register(Config.PREFIX + "info",  "Info sistem bot",          "Utility", new InfoHandler());
        router.register(Config.PREFIX + "help",  "Tampilkan semua perintah", "Utility", helpHandler);

        // ── Interactive Button Callbacks ─────────────────────────────────
        router.registerInteractive("ping_cmd", new PingHandler());
        router.registerInteractive("info_cmd", new InfoHandler());
        router.registerInteractive("help_cmd", helpHandler);
        router.registerInteractive("menu_cmd", new MenuHandler());

        Serialize.success("Registered " + router.getCommands().size() + " commands, "
                + router.getInteractiveCount() + " interactive handlers");
    }

    /** Setup listeners and start receiving messages. */
    public void start() {
        client.listener(new JaWaClient.Listener() {
            @Override
            public void onQr(List<String> qrStrings) {
                Serialize.info("Scan QR code to pair:");
                id.jawa.util.QrTerminal.render(qrStrings.getFirst());
            }

            @Override
            public void onConnected() {
                Serialize.success("Connected to WhatsApp!");
                Serialize.divider();
            }

            @Override
            public void onMessage(MessageReceiver.Decoded decoded) {
                handleMessage(decoded);
            }

            @Override
            public void onTerminated(JaWaClient.TerminationReason reason, String detail, boolean permanent) {
                Serialize.error("Disconnected: " + reason + " (detail=" + detail + ", permanent=" + permanent + ")");
            }
        });

        try {
            client.connect();
        } catch (Exception e) {
            Serialize.error("Connection failed", e);
        }
    }

    /** Route an incoming decoded message to the appropriate handler. */
    private void handleMessage(MessageReceiver.Decoded decoded) {
        try {
            String sender = decoded.senderJid();
            String group = decoded.groupJid();
            String chat = group != null ? group : sender;
            boolean isGroup = group != null;

            // Log incoming
            String text = decoded.text();
            String interactiveId = decoded.interactive() != null ? decoded.interactive().selectedId() : null;
            String interactiveType = decoded.interactive() != null ? decoded.interactive().kind() : null;
            Serialize.incoming(sender, chat, text != null ? text : (interactiveId != null ? "⚡ " + interactiveId : null));

            // Skip messages with no text and no interactive content
            if ((text == null || text.isBlank()) && (interactiveId == null || interactiveId.isBlank())) {
                return;
            }

            // Build context
            var ctx = new Context(
                    sender, chat, decoded.msgId(),
                    text, interactiveType, interactiveId,
                    isGroup, decoded.message(), client);

            // Route
            router.route(ctx);

        } catch (Exception e) {
            Serialize.error("Handler exception", e);
        }
    }
}
