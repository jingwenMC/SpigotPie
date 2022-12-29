package top.jingwenmc.spigotpie.bungee.command;

import top.jingwenmc.spigotpie.common.command.CommandItem;
import top.jingwenmc.spigotpie.common.command.CommandTreeNode;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@PieComponent
public class CommandManager {
    private CommandTreeNode root = new CommandTreeNode(null,CommandTreeNode.ROOT_NODE_PATH,null);

    protected void addCommandNode(String path, Consumer<CommandItem> consumer) {
        root.addCommandNode(path, consumer);
    }
}
