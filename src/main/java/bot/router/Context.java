// SPDX-License-Identifier: GPL-3.0-or-later
package bot.router;

import id.jawa.core.JaWaClient;
import id.jawa.message.MessageEncoder;
import id.jawa.proto.Wa;

import java.util.concurrent.CompletableFuture;

/**
 * Immutable message context passed to handlers.
 * Wraps the incoming message data and provides convenient reply helpers.
 *
 * @param senderJid       JID of the message sender
 * @param chatJid         chat JID (group JID if group, sender JID if DM)
 * @param messageId       stanza message ID
 * @param text            extracted text content (may be null)
 * @param interactiveType button click type (may be null)
 * @param interactiveId   button click ID (may be null)
 * @param isGroup         true if the message is from a group
 * @param rawMessage      the raw protobuf message
 * @param client          JaWa client for sending replies
 */
public record Context(
        String senderJid,
        String chatJid,
        String messageId,
        String text,
        String interactiveType,
        String interactiveId,
        boolean isGroup,
        Wa.Message rawMessage,
        JaWaClient client
) {

    // ── Reply Helpers ────────────────────────────────────────────────────

    /**
     * Reply with a {@link Wa.Message}, quoting the trigger message.
     *
     * @return CompletableFuture resolving to the sent message ID
     */
    public CompletableFuture<String> reply(Wa.Message msg) {
        String quotedText = text != null ? text : (interactiveId != null ? interactiveId : "");
        Wa.Message quoted = MessageEncoder.quote(msg, messageId, senderJid, quotedText);
        return client.sendMessage(chatJid, quoted);
    }

    /**
     * Reply with a plain text message, quoting the trigger.
     *
     * @return CompletableFuture resolving to the sent message ID
     */
    public CompletableFuture<String> reply(String text) {
        return reply(MessageEncoder.text(text));
    }

    /**
     * Send a reaction emoji to the trigger message.
     */
    public void react(String emoji) {
        var reaction = Wa.Message.newBuilder()
                .setReactionMessage(Wa.Message.ReactionMessage.newBuilder()
                        .setKey(Wa.MessageKey.newBuilder()
                                .setRemoteJid(chatJid)
                                .setId(messageId)
                                .setParticipant(isGroup ? senderJid : "")
                                .setFromMe(false))
                        .setText(emoji)
                        .setSenderTimestampMs(System.currentTimeMillis()))
                .build();
        client.sendMessage(chatJid, reaction);
    }

    // ── Command Helpers ──────────────────────────────────────────────────

    /** Check if the text matches a command (case-insensitive). */
    public boolean isCommand(String cmd) {
        return text != null && text.trim().toLowerCase().startsWith(cmd.toLowerCase());
    }

    /** Get the arguments after the command (everything after first space). */
    public String commandArgs() {
        if (text == null) return "";
        int space = text.indexOf(' ');
        return space >= 0 ? text.substring(space + 1).trim() : "";
    }
}
