// SPDX-License-Identifier: GPL-3.0-or-later
package bot.router;

import bot.Serialize;
import bot.cmd.Cmd;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Command routing engine. Maps text commands and interactive button IDs
 * to their respective handlers.
 *
 * <p>Usage:
 * <pre>{@code
 *   router.register(".ping", "Check bot", "General", new PingCmd());
 *   router.registerInteractive("ping_cmd", new PingCmd());
 *   router.route(ctx);
 * }</pre>
 */
public final class Router {

    private final Map<String, CommandInfo> commands = new LinkedHashMap<>();
    private final Map<String, Cmd> interactiveHandlers = new LinkedHashMap<>();

    /** Register a text command handler. */
    public void register(String command, String description, String category, Cmd handler) {
        commands.put(command.toLowerCase(), new CommandInfo(command, description, category, handler));
    }

    /** Register an interactive button click handler by ID. */
    public void registerInteractive(String id, Cmd handler) {
        interactiveHandlers.put(id, handler);
    }

    /**
     * Route an incoming context to the appropriate handler.
     *
     * <ol>
     *   <li>Check interactive button clicks first</li>
     *   <li>Then match text commands</li>
     * </ol>
     */
    public void route(Context ctx) {
        try {
            // 1. Interactive button clicks
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

            // 2. Text commands
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

            // Not a command — silently ignore
        } catch (Exception e) {
            Serialize.error("Handler error", e);
        }
    }

    /** Get all registered commands (unmodifiable). */
    public Map<String, CommandInfo> getCommands() {
        return Collections.unmodifiableMap(commands);
    }

    /** Get commands grouped by category. */
    public Map<String, List<CommandInfo>> getCategories() {
        return commands.values().stream()
                .collect(Collectors.groupingBy(
                        CommandInfo::category,
                        LinkedHashMap::new,
                        Collectors.toList()));
    }

    /** Get the number of registered interactive handlers. */
    public int getInteractiveCount() {
        return interactiveHandlers.size();
    }
}
