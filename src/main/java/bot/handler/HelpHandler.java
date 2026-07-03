// SPDX-License-Identifier: GPL-3.0-or-later
package bot.handler;

import bot.Config;
import bot.Serialize;
import bot.router.CommandInfo;
import bot.router.Context;
import bot.router.Router;

import java.util.List;
import java.util.Map;

/**
 * Help handler. Lists all registered commands grouped by category.
 *
 * <p>Trigger: {@code .help} or interactive {@code help_cmd}
 */
public final class HelpHandler implements Handler {

    private final Router router;

    public HelpHandler(Router router) {
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
