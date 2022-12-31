package top.jingwenmc.spigotpie.spigot;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import top.jingwenmc.spigotpie.spigot.command.CommandManager;
import top.jingwenmc.spigotpie.common.PieEnvironment;
import top.jingwenmc.spigotpie.common.SpigotPie;
import top.jingwenmc.spigotpie.common.command.CommandTreeNode;
import top.jingwenmc.spigotpie.common.instance.SimpleInstanceManager;

import java.lang.reflect.Field;
import java.util.logging.Level;

public class SpigotPieSpigot extends JavaPlugin {

    private static JavaPlugin pluginInstance;

    public static void inject(JavaPlugin plugin,String... filterPackagePath) {
        pluginInstance = plugin;
        try {
            SpigotPie.loadPlugin(
                    PieEnvironment.builder()
                            .bungeeCord(false)
                            .asDedicatePlugin(false)
                            .filterPackagePath(filterPackagePath)
                            .workFolder(pluginInstance.getDataFolder())
                            .logger(plugin.getLogger())
                            .build());
            postLoad();
        } catch (Exception e) {
            throw new RuntimeException("Exception during PostLoad:",e);
        }
    }

    public static void postLoad() throws NoSuchFieldException, IllegalAccessException {
        SimpleCommandMap commandMap;
        SimplePluginManager pluginManager = (SimplePluginManager) pluginInstance.getServer().getPluginManager();
        Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);
        CommandManager commandManager = (CommandManager) SimpleInstanceManager.getDeclaredInstance(CommandManager.class);
        assert commandManager != null;
        for(String commandName : commandManager.getAllCommands()) {
            commandMap.register("pie_"+pluginInstance.getName().toLowerCase(), new Command(commandName) {
                @Override
                public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                    CommandTreeNode node = commandManager.getNode(commandName,args);
                    if(node.isRoot()) {
                        sender.sendMessage(ChatColor.RED+"指令未找到!");//TODO: Localized Message
                    } else {
                        commandManager.invoke(sender,commandName,args);
                    }
                    return true;
                }
            });
        }
        pluginInstance.getLogger().log(Level.INFO,"[Pie]Command(s) registered.");
        pluginInstance.getLogger().log(Level.INFO,"[Pie]Total: "+commandManager.getAllCommands().length+" node(s)");
    }

    @Override
    public void onLoad() {
        System.err.println("Spigot Pie cannot act as a dedicate plugin currently!");
    }

    @Override
    public void onEnable() {
        System.err.println("Spigot Pie cannot act as a dedicate plugin currently!");
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
