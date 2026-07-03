// SPDX-License-Identifier: MIT
package bot.cmd.general;

import bot.cmd.Cmd;
import bot.router.Context;
import id.jawa.message.MessageEncoder;

import java.util.ArrayList;
import java.util.List;

public final class MenuCmd implements Cmd {

    @Override
    public String cmd() {
        return "menu";
    }

    @Override
    public String desc() {
        return "Tampilkan menu bot";
    }

    @Override
    public String tag() {
        return "general";
    }

    @Override
    public String[] alias() {
        return new String[]{"m", "help"};
    }

    @Override
    public void handle(Context ctx) throws Exception {
        var buttons = new ArrayList<MessageEncoder.CtaButton>();
        buttons.add(MessageEncoder.CtaButton.quickReply("🏓 Ping", "ping_cmd"));
        buttons.add(MessageEncoder.CtaButton.quickReply("ℹ️ Info", "info_cmd"));

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
