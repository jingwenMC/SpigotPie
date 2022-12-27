package top.jingwenmc.spigotpie.bungee.command;

import top.jingwenmc.spigotpie.common.command.CommandTreeNode;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

import java.util.concurrent.ConcurrentHashMap;

@PieComponent
public class CommandManager {
    private CommandTreeNode root = new CommandTreeNode(null,"::pie-root::",null);

    protected void addCommandNode(CommandTreeNode node) {

    }
}
