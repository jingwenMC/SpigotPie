package top.jingwenmc.spigotpie.common.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class CommandSender {
    private String name;

    public abstract boolean hasPermission(String permission);

    public abstract void sendMessage(String message);
}
