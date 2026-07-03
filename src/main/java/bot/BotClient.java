// SPDX-License-Identifier: MIT
package bot;

import bot.cmd.general.*;
import bot.cmd.utility.*;
import bot.cmd.owner.*;
import bot.cmd.test.*;
import bot.router.Context;
import bot.router.Router;
import id.jawa.core.JaWaClient;
import id.jawa.message.MessageReceiver;

import java.util.List;

public final class BotClient {

    private final JaWaClient client;
    private final Router router;

    public BotClient(JaWaClient client) {
        this.client = client;
        this.router = new Router();
        registerHandlers();
    }

    private void registerHandlers() {
        var helpCmd = new HelpCmd(router);

        router.register(Config.PREFIX, new MenuCmd());
        router.register(Config.PREFIX, new PingCmd());
        router.register(Config.PREFIX, new InfoCmd());
        router.register(Config.PREFIX, helpCmd);
        router.register(Config.PREFIX, new ExecCmd());
        router.register(Config.PREFIX, new TestCmd());

        router.registerInteractive("ping_cmd", new PingCmd());
        router.registerInteractive("info_cmd", new InfoCmd());
        router.registerInteractive("help_cmd", helpCmd);
        router.registerInteractive("menu_cmd", new MenuCmd());

        Logger.success("Registered " + router.getCommands().size() + " commands (including aliases), "
                + router.getInteractiveCount() + " interactive handlers");
    }

    public void start() {
        client.listener(new JaWaClient.Listener() {
            @Override
            public void onQr(List<String> qrStrings) {
                System.out.print("\n[Pairing] Enter the BOT phone number (e.g. 6283192674554): ");
                java.util.Scanner scanner = new java.util.Scanner(System.in);
                String phone = null;
                if (scanner.hasNextLine()) {
                    phone = scanner.nextLine().trim();
                }
                if (phone != null && !phone.isBlank()) {
                    Logger.info("Requesting pairing code for: " + phone);
                    client.requestPairingCode(phone, null).whenComplete((code, err) -> {
                        if (err != null) {
                            Logger.error("Pairing code request failed", err);
                            return;
                        }
                        String formattedCode = code.substring(0, 4) + "-" + code.substring(4);
                        System.out.println("\n" + Logger.BOLD + Logger.GREEN 
                            + "====================================" + Logger.RESET);
                        System.out.println("  " + Logger.BOLD + "Pairing Code: " + Logger.YELLOW + formattedCode + Logger.RESET);
                        System.out.println(Logger.BOLD + Logger.GREEN 
                            + "====================================" + Logger.RESET + "\n");
                    });
                } else {
                    Logger.info("No phone number entered, scanning via QR:");
                    id.jawa.util.QrTerminal.render(qrStrings.getFirst());
                }
            }

            @Override
            public void onConnected() {
                Logger.success("Connected to WhatsApp!");
                Logger.divider();
            }

            @Override
            public void onMessage(MessageReceiver.Decoded decoded) {
                handleMessage(decoded);
            }

            @Override
            public void onTerminated(JaWaClient.TerminationReason reason, String detail, boolean permanent) {
                Logger.error("Disconnected: " + reason + " (detail=" + detail + ", permanent=" + permanent + ")");
            }
        });

        try {
            client.connect();
        } catch (Exception e) {
            Logger.error("Connection failed", e);
        }
    }

    private boolean isSelf(String jidStr) {
        if (jidStr == null || client.creds() == null) return false;
        String resolved = client.resolvePnJid(jidStr);
        id.jawa.util.Jid jid = id.jawa.util.Jid.parse(resolved);
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

    private void handleMessage(MessageReceiver.Decoded decoded) {
        try {
            String sender = decoded.senderJid();
            String group = decoded.groupJid();
            String chat = group != null ? group : sender;
            boolean isGroup = group != null;

            String text = decoded.text();
            String interactiveId = decoded.interactive() != null ? decoded.interactive().selectedId() : null;
            String interactiveType = decoded.interactive() != null ? decoded.interactive().kind() : null;

            boolean isFromSelf = isSelf(sender);
            if (Config.SELF && !isFromSelf) {
                return;
            }

            Logger.incoming(sender, chat, text != null ? text : (interactiveId != null ? "⚡ " + interactiveId : null));

            if ((text == null || text.isBlank()) && (interactiveId == null || interactiveId.isBlank())) {
                return;
            }

            var ctx = new Context(
                    sender, chat, decoded.msgId(),
                    text, interactiveType, interactiveId,
                    isGroup, decoded.message(), client);

            router.route(ctx);

        } catch (Exception e) {
            Logger.error("Handler exception", e);
        }
    }
}
