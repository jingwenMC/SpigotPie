package top.jingwenmc.spigotpie.spigot.command;

import org.bukkit.command.CommandSender;
import top.jingwenmc.spigotpie.common.command.CommandItem;
import top.jingwenmc.spigotpie.common.command.CommandTreeNode;
import top.jingwenmc.spigotpie.common.command.PieCommand;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Platform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;

@PieComponent(platform = Platform.SPIGOT)
public class CommandManager {
    private final CommandTreeNode root = new CommandTreeNode(null,CommandTreeNode.ROOT_NODE_PATH,null,null);

    protected void addCommandNode(String path, Consumer<CommandItem> consumer, PieCommand pieCommand) {
        root.addCommandNode(path, consumer, pieCommand);
    }

    public String[] getAllCommands() {
        return root.getTreeMap().keySet().toArray(new String[0]);
    }

    public CommandTreeNode getNode(String commandName,String[] args) {
        ArrayList<String> arrayList = new ArrayList<>(Collections.singletonList(commandName));
        arrayList.addAll(Arrays.asList(args));
        return root.getCommandNode(root,arrayList.toArray(new String[0]));
    }

    public void invoke(CommandSender sender, String commandName, String[] args) {
        top.jingwenmc.spigotpie.common.command.CommandSender sender1 = new top.jingwenmc.spigotpie.common.command.CommandSender(sender.getName()) {
            @Override
            public boolean hasPermission(String permission) {
                return sender.hasPermission(permission);
            }

            @Override
            public void sendMessage(String message) {
                sender.sendMessage(message);
            }
        };
        ArrayList<String> arrayList = new ArrayList<>(Collections.singletonList(commandName));
        arrayList.addAll(Arrays.asList(args));
        CommandTreeNode node = root.getCommandNode(root,arrayList.toArray(new String[0]));
        String[] fArgs = root.parseArgs(arrayList.toArray(new String[0]));
        node.invoke(sender1,fArgs);
    }
}
