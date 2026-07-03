// SPDX-License-Identifier: GPL-3.0-or-later
package bot.router;

import bot.cmd.Cmd;

public record CommandInfo(String command, String description, String category, Cmd handler) {}
