// SPDX-License-Identifier: GPL-3.0-or-later
package bot.cmd.utility;

import bot.Config;
import bot.Serialize;
import bot.cmd.Cmd;
import bot.router.Context;

/**
 * System information command.
 *
 * <p>Trigger: {@code .info} or interactive {@code info_cmd}
 */
public final class InfoCmd implements Cmd {

    @Override
    public void handle(Context ctx) throws Exception {
        Runtime rt = Runtime.getRuntime();
        long usedMem = rt.totalMemory() - rt.freeMemory();
        long uptime = System.currentTimeMillis() - Config.START_TIME;

        String info = "ℹ️ " + Serialize.bold("Bot Information") + "\n\n"
                + Serialize.field("Name", Config.BOT_NAME) + "\n"
                + Serialize.field("Version", Config.VERSION) + "\n"
                + Serialize.field("Uptime", Serialize.code(Serialize.formatUptime(uptime))) + "\n"
                + Serialize.field("Java", System.getProperty("java.version")) + "\n"
                + Serialize.field("OS", System.getProperty("os.name") + " " + System.getProperty("os.arch")) + "\n"
                + Serialize.field("Memory", Serialize.formatBytes(usedMem) + " / " + Serialize.formatBytes(rt.totalMemory())) + "\n"
                + Serialize.field("Processors", String.valueOf(rt.availableProcessors()));

        ctx.reply(info);
    }
}
