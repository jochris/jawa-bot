// SPDX-License-Identifier: GPL-3.0-or-later
package bot.router;

import bot.Serialize;
import bot.cmd.Cmd;

import java.util.*;
import java.util.stream.Collectors;

public final class Router {

    private final Map<String, CommandInfo> commands = new LinkedHashMap<>();
    private final Map<String, Cmd> interactiveHandlers = new LinkedHashMap<>();

    public void register(String prefix, Cmd cmd) {
        String primaryTrigger = prefix + cmd.cmd().toLowerCase();
        CommandInfo info = new CommandInfo(primaryTrigger, cmd.desc(), cmd.tag(), cmd);
        commands.put(primaryTrigger, info);

        for (String a : cmd.alias()) {
            commands.put(prefix + a.toLowerCase(), info);
        }
    }

    public void registerInteractive(String id, Cmd handler) {
        interactiveHandlers.put(id, handler);
    }

    public void route(Context ctx) {
        try {
            String interactiveId = ctx.interactiveId();
            if (interactiveId != null && !interactiveId.isBlank()) {
                Cmd handler = interactiveHandlers.get(interactiveId);
                if (handler != null) {
                    Serialize.debug("Interactive route: " + interactiveId);
                    handler.handle(ctx);
                    return;
                }
                Serialize.warn("Unknown interactive ID: " + interactiveId);
                return;
            }

            String text = ctx.text();
            if (text == null || text.isBlank()) return;
            String lower = text.trim().toLowerCase();

            for (var entry : commands.entrySet()) {
                if (lower.startsWith(entry.getKey())) {
                    Serialize.debug("Command route: " + entry.getKey());
                    entry.getValue().handler().handle(ctx);
                    return;
                }
            }
        } catch (Exception e) {
            Serialize.error("Handler error", e);
        }
    }

    public Map<String, CommandInfo> getCommands() {
        return Collections.unmodifiableMap(commands);
    }

    public Map<String, List<CommandInfo>> getCategories() {
        Map<String, List<CommandInfo>> grouped = new LinkedHashMap<>();
        for (CommandInfo info : commands.values()) {
            grouped.computeIfAbsent(info.category(), k -> new ArrayList<>()).add(info);
        }
        for (var list : grouped.values()) {
            Set<Cmd> seen = new LinkedHashSet<>();
            list.removeIf(info -> !seen.add(info.handler()));
        }
        return grouped;
    }

    public int getInteractiveCount() {
        return interactiveHandlers.size();
    }
}
