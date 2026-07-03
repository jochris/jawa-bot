// SPDX-License-Identifier: GPL-3.0-or-later
package bot.cmd.general;

import bot.Serialize;
import bot.cmd.Cmd;
import bot.router.Context;

/**
 * Ping-pong command that measures message latency.
 *
 * <p>Trigger: {@code .ping} or interactive {@code ping_cmd}
 */
public final class PingCmd implements Cmd {

    @Override
    public void handle(Context ctx) throws Exception {
        long start = System.currentTimeMillis();
        ctx.react("⚡");
        long latency = System.currentTimeMillis() - start;
        ctx.reply("🏓 " + Serialize.bold("Pong!") + "\n\n"
                + Serialize.field("Latency", Serialize.code(latency + "ms")));
    }
}
