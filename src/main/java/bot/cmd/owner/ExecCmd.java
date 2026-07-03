// SPDX-License-Identifier: MIT
package bot.cmd.owner;

import bot.Config;
import bot.Serialize;
import bot.cmd.Cmd;
import bot.router.Context;
import id.jawa.util.Jid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class ExecCmd implements Cmd {

    @Override
    public String cmd() {
        return "exec";
    }

    @Override
    public String desc() {
        return "Jalankan perintah shell (Khusus Owner)";
    }

    @Override
    public String tag() {
        return "owner";
    }

    @Override
    public String[] alias() {
        return new String[]{"$"};
    }

    @Override
    public void handle(Context ctx) throws Exception {
        String senderPnJid = ctx.client().resolvePnJid(ctx.senderJid());
        Jid sender = Jid.parse(senderPnJid);
        if (sender == null || !sender.user().equals(Config.OWNER_NUMBER)) {
            ctx.reply("❌ " + Serialize.bold("Akses Ditolak!") + "\n\nHanya owner yang dapat menggunakan perintah ini.");
            return;
        }

        String command = ctx.commandArgs();
        if (command == null || command.isBlank()) {
            ctx.reply("⚠️ " + Serialize.bold("Perintah Kosong!") + "\n\nFormat: " + Serialize.code(".exec <command>"));
            return;
        }

        ctx.react("⏳");

        try {
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            List<String> pbArgs = new ArrayList<>();
            if (isWindows) {
                pbArgs.add("cmd.exe");
                pbArgs.add("/c");
            } else {
                pbArgs.add("bash");
                pbArgs.add("-c");
            }
            pbArgs.add(command);

            ProcessBuilder pb = new ProcessBuilder(pbArgs);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            String res = output.toString().trim();
            if (res.isEmpty()) {
                res = "[No Output]";
            }

            ctx.reply("💻 " + Serialize.bold("Exec Result") + " (Exit: " + exitCode + ")\n\n"
                    + Serialize.mono(res));
            ctx.react("✅");

        } catch (Exception e) {
            ctx.reply("❌ " + Serialize.bold("Exec Error") + "\n\n" + Serialize.code(e.getMessage()));
            ctx.react("❌");
        }
    }
}
