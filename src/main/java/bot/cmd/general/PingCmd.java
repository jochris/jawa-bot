// SPDX-License-Identifier: MIT
package bot.cmd.general;

import bot.Serialize;
import bot.cmd.Cmd;
import bot.router.Context;

public final class PingCmd implements Cmd {

    @Override
    public String cmd() {
        return "ping";
    }

    @Override
    public String desc() {
        return "Cek apakah bot aktif";
    }

    @Override
    public String tag() {
        return "general";
    }

    @Override
    public String[] alias() {
        return new String[]{"p", "pong"};
    }

    @Override
    public void handle(Context ctx) throws Exception {
        long start = System.currentTimeMillis();
        ctx.react("⚡");
        long latency = System.currentTimeMillis() - start;
        ctx.reply("🏓 " + Serialize.bold("Pong!") + "\n\n"
                + Serialize.field("Latency", Serialize.code(latency + "ms")));
    }
}
