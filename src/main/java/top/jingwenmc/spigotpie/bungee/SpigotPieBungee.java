package top.jingwenmc.spigotpie.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import top.jingwenmc.spigotpie.bungee.command.CommandManager;
import top.jingwenmc.spigotpie.common.PieEnvironment;
import top.jingwenmc.spigotpie.common.SpigotPie;
import top.jingwenmc.spigotpie.common.command.CommandTreeNode;
import top.jingwenmc.spigotpie.common.instance.SimpleInstanceManager;

import java.util.logging.Level;

public final class SpigotPieBungee extends Plugin {

    private static Plugin pluginInstance = null;

    public static void inject(Plugin plugin,String... filterPackagePath) {
        pluginInstance = plugin;
        try {
            SpigotPie.loadPlugin(
                    PieEnvironment.builder()
                            .bungeeCord(true)
                            .asDedicatePlugin(false)
                            .filterPackagePath(filterPackagePath)
                            .workFolder(pluginInstance.getDataFolder())
                            .logger(plugin.getLogger())
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        postLoad();
    }

    public static void postLoad() {
        CommandManager commandManager = (CommandManager) SimpleInstanceManager.getDeclaredInstance(CommandManager.class);
        assert commandManager != null;
        for(String commandName : commandManager.getAllCommands()) {
            pluginInstance.getProxy().getPluginManager().registerCommand(pluginInstance, new Command(commandName) {
                @Override
                public void execute(CommandSender sender, String[] args) {
                    CommandTreeNode node = commandManager.getNode(commandName,args);
                    if(node.isRoot()) {
                        TextComponent tc = new TextComponent("指令未找到!");//TODO: Localized Message
                        tc.setColor(ChatColor.RED);
                        sender.sendMessage(tc);
                    } else {
                        commandManager.invoke(sender,commandName,args);
                    }
                }
            });
        }
        pluginInstance.getLogger().log(Level.INFO,"[Pie-BC]Command(s) registered.");
        pluginInstance.getLogger().log(Level.INFO,"[Pie-BC]Total: "+commandManager.getAllCommands().length+" node(s)");
    }

    @Override
    public void onLoad() {
        System.err.println("Spigot Pie cannot act as a dedicate plugin currently!");
        // Plugin startup logic
    }

    @Override
    public void onEnable() {
        System.err.println("Spigot Pie cannot act as a dedicate plugin currently!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
