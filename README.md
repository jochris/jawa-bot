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
    └── handler/
        ├── Handler.java                     # Handler functional interface
        ├── MenuHandler.java                 # Interactive menu (.menu)
        ├── PingHandler.java                 # Ping-pong (.ping)
        ├── InfoHandler.java                 # System info (.info)
        └── HelpHandler.java                # Help listing (.help)
```

## 🚀 Quick Start

```bash
# 1. Build
mvn compile

# 2. Run (first time - scan QR)
mvn exec:java -Dbot.session=sessions/mybot.session

# 3. Run (reconnect)
mvn exec:java -Dbot.session=sessions/mybot.session
```

## ➕ Adding a New Command

1. Create `src/main/java/bot/handler/MyHandler.java`:

```java
// SPDX-License-Identifier: GPL-3.0-or-later
package bot.handler;

import bot.router.Context;

public final class MyHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Exception {
        ctx.reply("Hello from my handler!");
    }
}
```

2. Register in `BotClient.java`:

```java
router.register(".mycommand", "My description", "MyCategory", new MyHandler());
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

| Field       | Default       | Description               |
|-------------|---------------|---------------------------|
| `BOT_NAME`  | `"JaWa Bot"`  | Bot display name          |
| `PREFIX`    | `"."`         | Command prefix            |
| `VERSION`   | `"1.0.0"`     | Bot version               |

## 📜 License

GPL-3.0-or-later
