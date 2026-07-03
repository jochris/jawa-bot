// SPDX-License-Identifier: GPL-3.0-or-later
package bot.cmd;

import bot.router.Context;

public interface Cmd {
    void handle(Context ctx) throws Exception;
    String cmd();
    String desc();
    String tag();
    String[] alias();
}
