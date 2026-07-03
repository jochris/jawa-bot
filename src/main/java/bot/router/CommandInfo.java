// SPDX-License-Identifier: MIT
package bot.router;

import bot.cmd.Cmd;

public record CommandInfo(String command, String description, String category, Cmd handler) {}
