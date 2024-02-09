package top.jingwenmc.spigotpie.common.command.message;

import top.jingwenmc.spigotpie.common.command.CommandItem;

import java.util.Map;

public interface CommandMessageHandler {
    void handleMessage(CommandItem item, MessageType type, Map<String,String> parameters);
}
