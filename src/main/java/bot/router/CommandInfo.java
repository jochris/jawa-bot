// SPDX-License-Identifier: GPL-3.0-or-later
package bot.router;

import bot.handler.Handler;

/**
 * Metadata for a registered command.
 *
 * @param command     the trigger text (e.g. ".ping")
 * @param description human-readable description
 * @param category    grouping category (e.g. "General", "Utility")
 * @param handler     the handler to invoke
 */
public record CommandInfo(String command, String description, String category, Handler handler) {}
