// SPDX-License-Identifier: GPL-3.0-or-later
package bot.handler;

import bot.router.Context;
import id.jawa.message.MessageEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Interactive menu handler. Displays a native WhatsApp flow menu
 * with quick reply buttons and a dropdown selector.
 *
 * <p>Trigger: {@code .menu} or interactive {@code menu_cmd}
 */
public final class MenuHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {
        var buttons = new ArrayList<MessageEncoder.CtaButton>();

        // Quick reply buttons
        buttons.add(MessageEncoder.CtaButton.quickReply("🏓 Ping", "ping_cmd"));
        buttons.add(MessageEncoder.CtaButton.quickReply("ℹ️ Info", "info_cmd"));

        // Dropdown menu
        var rows = List.of(
                new MessageEncoder.ListRow("help_cmd", "📚 Help", "Tampilkan semua perintah"),
                new MessageEncoder.ListRow("menu_cmd", "🔄 Menu", "Tampilkan menu ini kembali")
        );
        var sections = List.of(new MessageEncoder.ListSection("Menu Lainnya", rows));
        buttons.add(MessageEncoder.CtaButton.singleSelect("📋 Lainnya...", sections));

        var interactiveMsg = MessageEncoder.interactiveCtaButtons(
                "*🤖 JaWa Bot Menu*\n\nPilih menu di bawah:",
                "Powered by JaWa",
                buttons);

        ctx.reply(interactiveMsg);
    }
}
