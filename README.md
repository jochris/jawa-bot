# 🤖 JaWa Bot Starter Kit

Modern WhatsApp bot starter kit powered by [JaWa](https://github.com/jochris/JaWa).

## 📁 Project Structure

```
jawa-bot/
├── pom.xml                                  # Maven config
├── README.md                                # This file
└── src/main/java/bot/
    ├── Main.java                            # Entry point
    ├── BotClient.java                       # Bot lifecycle & event routing
    ├── Config.java                          # Bot configuration constants
    ├── Serialize.java                       # Pretty logging & formatting
    ├── router/
    │   ├── Router.java                      # Command routing engine
    │   ├── Context.java                     # Message context with helpers
    │   └── CommandInfo.java                 # Command metadata record
    └── cmd/
        ├── Cmd.java                         # Command functional interface
        ├── general/
        │   ├── MenuCmd.java                 # Interactive menu (.menu)
        │   └── PingCmd.java                 # Ping-pong (.ping)
        └── utility/
            ├── InfoCmd.java                 # System info (.info)
            └── HelpCmd.java                 # Help listing (.help)
```

## 🚀 Quick Start

```bash
# 1. Build
mvn compile

# 2. Run (launches pairing code flow automatically if no session exists)
mvn exec:java -Dbot.session=sessions/mybot.session
```

## ➕ Adding a New Command

1. Create a command class under the appropriate category folder, e.g. `src/main/java/bot/cmd/general/MyCmd.java`:

```java
// SPDX-License-Identifier: GPL-3.0-or-later
package bot.cmd.general;

import bot.cmd.Cmd;
import bot.router.Context;

public final class MyCmd implements Cmd {
    @Override
    public void handle(Context ctx) throws Exception {
        ctx.reply("Hello from my command!");
    }
}
```

2. Register in `BotClient.java`:

```java
router.register(Config.PREFIX + "mycommand", "My description", "General", new MyCmd());
```

## 📖 Context API

| Method                  | Description                          |
|-------------------------|--------------------------------------|
| `ctx.reply(String)`     | Reply with text (quotes trigger)     |
| `ctx.reply(Wa.Message)` | Reply with any message type          |
| `ctx.react(String)`     | React to trigger with emoji          |
| `ctx.text()`            | Get message text                     |
| `ctx.commandArgs()`     | Get args after command               |
| `ctx.senderJid()`       | Sender JID                           |
| `ctx.chatJid()`         | Chat JID (group or DM)              |
| `ctx.isGroup()`         | Is from group?                       |
| `ctx.client()`          | Access JaWaClient directly           |

## ⚙️ Configuration

Edit `Config.java` to customize:

| Field          | Default             | Description                                     |
|----------------|---------------------|-------------------------------------------------|
| `BOT_NAME`     | `"JaWa Bot"`        | Bot display name                                |
| `PREFIX`       | `"."`               | Command prefix                                  |
| `VERSION`      | `"1.0.0"`           | Bot version                                     |
| `SELF`         | `false`             | Only respond to bot's own pairing number        |
| `OWNER_NUMBER` | `"62895416602000"`  | Owner's phone number for auto-pairing           |

## 📜 License

GPL-3.0-or-later
