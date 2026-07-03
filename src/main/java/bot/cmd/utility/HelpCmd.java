// SPDX-License-Identifier: GPL-3.0-or-later
package bot.cmd.utility;

import bot.Config;
import bot.Serialize;
import bot.cmd.Cmd;
import bot.router.CommandInfo;
import bot.router.Context;
import bot.router.Router;

import java.util.List;
import java.util.Map;

/**
 * Help command listing all registered text commands.
 *
 * <p>Trigger: {@code .help} or interactive {@code help_cmd}
 */
public final class HelpCmd implements Cmd {

    private final Router router;

    public HelpCmd(Router router) {
        this.router = router;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        var categories = router.getCategories();
        var sb = new StringBuilder();
        sb.append("📚 ").append(Serialize.bold("Help Menu")).append("\n");
        sb.append(Serialize.italic("Prefix: " + Config.PREFIX)).append("\n");

        for (Map.Entry<String, List<CommandInfo>> entry : categories.entrySet()) {
            sb.append("\n").append(Serialize.bold(entry.getKey())).append("\n");
            for (CommandInfo cmd : entry.getValue()) {
                sb.append("  ").append(Serialize.code(cmd.command()))
                  .append(" — ").append(cmd.description()).append("\n");
            }
        }

        ctx.reply(sb.toString().trim());
    }
}
