// SPDX-License-Identifier: GPL-3.0-or-later
package bot.handler;

/**
 * Functional interface for bot command handlers.
 * Each handler processes a single message context.
 */
@FunctionalInterface
public interface Handler {
    void handle(bot.router.Context ctx) throws Exception;
}
