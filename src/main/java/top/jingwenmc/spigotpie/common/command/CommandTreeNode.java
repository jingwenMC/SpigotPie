package top.jingwenmc.spigotpie.common.command;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Data
@AllArgsConstructor
public class CommandTreeNode {
    private final CommandTreeNode parent;

    private final String path;

    private final Consumer<CommandItem> consumer;

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
}
