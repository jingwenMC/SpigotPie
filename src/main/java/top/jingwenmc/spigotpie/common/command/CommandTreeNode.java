package top.jingwenmc.spigotpie.common.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.ChatColor;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Data
@AllArgsConstructor
public class CommandTreeNode {
    private final CommandTreeNode parent;

    private final String path;

    private Consumer<CommandItem> consumer;

    private final Map<String, CommandTreeNode> treeMap = new ConcurrentHashMap<>();

    public boolean addCommandNode(CommandTreeNode node) {
        if(node.parent==this) {
            treeMap.put(node.path,node);
            return true;
        } else {
            for(CommandTreeNode treeNode: treeMap.values()) {
                if(treeNode.addCommandNode(node))return true;
            }
            return false;
        }
    }

    public boolean isRoot() {
        return ROOT_NODE_PATH.equalsIgnoreCase(path);
    }

    public void addCommandNode(String path,Consumer<CommandItem> consumer) {
        ArrayList<String> paths = new ArrayList<>(Arrays.asList(path.split(" ")));
        if(this.path.equalsIgnoreCase(ROOT_NODE_PATH)) {
            if(paths.size() == 0) {
                throw new IllegalArgumentException("Illegal path append to root/manager node");
            }
        }
        if(paths.size()>0) {
            String sub = paths.get(0);
            paths.remove(0);
            StringJoiner sj = new StringJoiner(" ");
            for (String s : paths) {
                sj.add(s);
            }
            CommandTreeNode node = treeMap.get(sub);
            if(node == null) {
                node = new CommandTreeNode(this,sub,commandItem -> commandItem.getSender().sendMessage(ChatColor.RED+"子指令未找到!请检查输入项目是否正确!")); //TODO: Localized message
                treeMap.put(sub,node);
            }
            node.addCommandNode(sj.toString(),consumer);
        } else {
            this.consumer = consumer;
        }
    }

    public CommandTreeNode getCommandNode(CommandTreeNode requestBy, String[] args) {
        ArrayList<String> paths = new ArrayList<>(Arrays.asList(args));
        if(paths.size() == 0) {
            return requestBy;
        }
        String sub = paths.get(0);
        paths.remove(0);
        CommandTreeNode node = treeMap.get(sub);
        if(node == null) {
            return requestBy;
        }
        return node.getCommandNode(this,paths.toArray(new String[0]));
    }

    public String[] parseArgs(String[] args) {
        ArrayList<String> paths = new ArrayList<>(Arrays.asList(args));
        if(paths.size() == 0) {
            return paths.toArray(new String[0]);
        }
        String sub = paths.get(0);
        paths.remove(0);
        CommandTreeNode node = treeMap.get(sub);
        if(node == null) {
            return args;
        }
        return node.parseArgs(paths.toArray(new String[0]));
    }

    public void invoke(CommandSender sender,String[] args) {
        consumer.accept(new CommandItem(sender,args));
    }

    public static final String ROOT_NODE_PATH = "::pie-root::";
}
