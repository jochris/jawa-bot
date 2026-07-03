// SPDX-License-Identifier: GPL-3.0-or-later
package bot.cmd;

import bot.router.Context;

/**
 * Functional interface for bot commands.
 * Every command implements this to handle incoming WA message contexts.
 */
@FunctionalInterface
public interface Cmd {
    void handle(Context ctx) throws Exception;
}
