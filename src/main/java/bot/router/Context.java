// SPDX-License-Identifier: MIT
package bot.router;

import id.jawa.core.JaWaClient;
import id.jawa.message.MessageEncoder;
import id.jawa.proto.Wa;

import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<String> reply(Wa.Message msg) {
        String quotedText = text != null ? text : (interactiveId != null ? interactiveId : "");
        Wa.Message quoted = MessageEncoder.quote(msg, messageId, senderJid, quotedText);
        return client.sendMessage(chatJid, quoted);
    }

    public CompletableFuture<String> reply(String text) {
        return reply(MessageEncoder.text(text));
    }

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

    public boolean isCommand(String cmd) {
        return text != null && text.trim().toLowerCase().startsWith(cmd.toLowerCase());
    }

    public String commandArgs() {
        if (text == null) return "";
        int space = text.indexOf(' ');
        return space >= 0 ? text.substring(space + 1).trim() : "";
    }
}
