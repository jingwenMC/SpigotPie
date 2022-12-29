package top.jingwenmc.spigotpie.common.command;

import lombok.AllArgsConstructor;
import lombok.Data;
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
                node = new CommandTreeNode(this,sub,null);
                treeMap.put(sub,node);
            }
            node.addCommandNode(sj.toString(),consumer);
        } else {
            this.consumer = consumer;
        }
    }

    public CommandTreeNode getCommandNode(String path) {
        ArrayList<String> paths = new ArrayList<>(Arrays.asList(path.split(" ")));
        if(paths.size() == 0) {
            return null;
        }
        if(paths.size() == 1) {
            return treeMap.get(paths.get(0));
        }
        String sub = paths.get(0);
        paths.remove(0);
        StringJoiner sj = new StringJoiner(" ");
        for (String s : paths) {
            sj.add(s);
        }
        CommandTreeNode node = treeMap.get(sub);
        if(node == null)return null;
        return getCommandNode(sj.toString());
    }

    public static final String ROOT_NODE_PATH = "::pie-root::";
}
