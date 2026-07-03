// SPDX-License-Identifier: MIT
package bot.cmd.test;

import bot.cmd.Cmd;
import bot.router.Context;
import id.jawa.message.MessageEncoder;
import id.jawa.message.MessageEncoder.CtaButton;
import id.jawa.message.MessageEncoder.ListRow;
import id.jawa.message.MessageEncoder.ListSection;

import java.util.ArrayList;
import java.util.List;

public final class TestCmd implements Cmd {

    @Override
    public String cmd() {
        return "test";
    }

    @Override
    public String desc() {
        return "Kirim berbagai tipe pesan interaktif untuk pengujian";
    }

    @Override
    public String tag() {
        return "test";
    }

    @Override
    public String[] alias() {
        return new String[]{"ts"};
    }

    @Override
    public void handle(Context ctx) throws Exception {
        ctx.react("🧪");

        List<CtaButton> buttons = new ArrayList<>();
        buttons.add(CtaButton.quickReply("🏓 Ping Test", "ping_cmd"));
        buttons.add(CtaButton.quickReply("ℹ️ Info Test", "info_cmd"));
        buttons.add(CtaButton.url("🌐 Kunjungi Google", "https://google.com"));
        buttons.add(CtaButton.copy("📋 Salin Kode Kupon", "JAWABOT-FREE-2026"));
        buttons.add(CtaButton.call("📞 Hubungi Owner", "+62895416602000"));

        List<ListRow> rows = List.of(
            new ListRow("ping_cmd", "🏓 Ping", "Menguji respon latency bot"),
            new ListRow("info_cmd", "ℹ️ Info", "Melihat spesifikasi sistem bot"),
            new ListRow("help_cmd", "📚 Help", "Menampilkan menu bantuan lengkap")
        );
        List<ListSection> sections = List.of(new ListSection("Pilihan Menu", rows));
        buttons.add(CtaButton.singleSelect("📋 Pilih Fitur...", sections));

        var interactiveMsg = MessageEncoder.interactiveCtaButtons(
            "*🧪 JaWa Bot Interactive Test*\n\nPesan ini berisi semua tipe komponen interaktif yang didukung:\n- Quick Reply Buttons\n- URL Redirection\n- Copy to Clipboard\n- E.164 Dialer Call\n- Single-Select Dropdown List",
            "Powered by JaWa Client Library",
            buttons
        );

        ctx.reply(interactiveMsg);
    }
}
