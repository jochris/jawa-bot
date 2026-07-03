// SPDX-License-Identifier: GPL-3.0-or-later
package bot;

import bot.cmd.general.*;
import bot.cmd.utility.*;
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
        var helpCmd = new HelpCmd(router);

        // ── Text Commands ────────────────────────────────────────────────
        router.register(Config.PREFIX + "menu",  "Tampilkan menu bot",       "General", new MenuCmd());
        router.register(Config.PREFIX + "ping",  "Cek apakah bot aktif",     "General", new PingCmd());
        router.register(Config.PREFIX + "info",  "Info sistem bot",          "Utility", new InfoCmd());
        router.register(Config.PREFIX + "help",  "Tampilkan semua perintah", "Utility", helpCmd);

        // ── Interactive Button Callbacks ─────────────────────────────────
        router.registerInteractive("ping_cmd", new PingCmd());
        router.registerInteractive("info_cmd", new InfoCmd());
        router.registerInteractive("help_cmd", helpCmd);
        router.registerInteractive("menu_cmd", new MenuCmd());

        Serialize.success("Registered " + router.getCommands().size() + " commands, "
                + router.getInteractiveCount() + " interactive handlers");
    }

    /** Setup listeners and start receiving messages. */
    public void start() {
        client.listener(new JaWaClient.Listener() {
            @Override
            public void onQr(List<String> qrStrings) {
                String phone = Config.OWNER_NUMBER;
                if (phone == null || phone.isBlank()) {
                    System.out.print("\n[Pairing] Enter your phone number (e.g. 62895416602000): ");
                    java.util.Scanner scanner = new java.util.Scanner(System.in);
                    if (scanner.hasNextLine()) {
                        phone = scanner.nextLine().trim();
                    }
                }
                if (phone != null && !phone.isBlank()) {
                    Serialize.info("Requesting pairing code for: " + phone);
                    client.requestPairingCode(phone, null).whenComplete((code, err) -> {
                        if (err != null) {
                            Serialize.error("Pairing code request failed", err);
                            return;
                        }
                        String formattedCode = code.substring(0, 4) + "-" + code.substring(4);
                        System.out.println("\n" + Serialize.BOLD + Serialize.GREEN 
                            + "====================================" + Serialize.RESET);
                        System.out.println("  " + Serialize.BOLD + "Pairing Code: " + Serialize.YELLOW + formattedCode + Serialize.RESET);
                        System.out.println(Serialize.BOLD + Serialize.GREEN 
                            + "====================================" + Serialize.RESET + "\n");
                    });
                } else {
                    Serialize.info("No phone number entered, scanning via QR:");
                    id.jawa.util.QrTerminal.render(qrStrings.getFirst());
                }
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

    /** Check if a given JID belongs to the bot itself (meJid or meLid). */
    private boolean isSelf(String jidStr) {
        if (jidStr == null || client.creds() == null) return false;
        id.jawa.util.Jid jid = id.jawa.util.Jid.parse(jidStr);
        if (jid == null) return false;

        if (client.creds().meJid != null) {
            id.jawa.util.Jid meJid = id.jawa.util.Jid.parse(client.creds().meJid);
            if (meJid != null && meJid.user().equals(jid.user())) {
                return true;
            }
        }
        if (client.creds().meLid != null) {
            id.jawa.util.Jid meLid = id.jawa.util.Jid.parse(client.creds().meLid);
            if (meLid != null && meLid.user().equals(jid.user())) {
                return true;
            }
        }
        return false;
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

            // Self-mode filtering
            boolean isFromSelf = isSelf(sender);
            if (Config.SELF && !isFromSelf) {
                // If SELF mode is enabled, ignore all messages not from self JIDs
                return;
            }

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
