// SPDX-License-Identifier: MIT
package bot.cmd;

import bot.router.Context;

public interface Cmd {
    void handle(Context ctx) throws Exception;
    String cmd();
    String desc();
    String tag();
    String[] alias();
}
