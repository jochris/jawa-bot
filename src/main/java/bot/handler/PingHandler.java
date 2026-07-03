// SPDX-License-Identifier: GPL-3.0-or-later
package bot.handler;

import bot.Serialize;
import bot.router.Context;

/**
 * Ping-pong handler. Measures response latency.
 *
 * <p>Trigger: {@code .ping} or interactive {@code ping_cmd}
 */
public final class PingHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {
        long start = System.currentTimeMillis();
        ctx.react("⚡");
        long latency = System.currentTimeMillis() - start;
        ctx.reply("🏓 " + Serialize.bold("Pong!") + "\n\n"
                + Serialize.field("Latency", Serialize.code(latency + "ms")));
    }
}
