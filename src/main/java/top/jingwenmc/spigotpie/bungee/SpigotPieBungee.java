package top.jingwenmc.spigotpie.bungee;

import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import top.jingwenmc.spigotpie.PieDistroConfigurations;
import top.jingwenmc.spigotpie.bungee.command.CommandManager;
import top.jingwenmc.spigotpie.bungee.configuration.BungeeConfigurationAdapter;
import top.jingwenmc.spigotpie.common.PieEnvironment;
import top.jingwenmc.spigotpie.common.SpigotPie;
import top.jingwenmc.spigotpie.common.command.CommandTreeNode;
import top.jingwenmc.spigotpie.common.instance.ObjectManager;
import top.jingwenmc.spigotpie.common.lang.PieLang;

import java.util.logging.Level;
import top.jingwenmc.spigotpie.bungee.metrics.Metrics;

public final class SpigotPieBungee extends Plugin {
    @Getter
    private static Plugin pluginInstance = null;
    @Getter
    private static Metrics metrics;
    public static void inject(Plugin plugin,boolean filterWhitelistMode,String... filterPackagePath) {
        pluginInstance = plugin;
        metrics = new Metrics(pluginInstance,17704);
        metrics.addCustomChart(new Metrics.SimplePie("spigot_pie_api_version", () -> String.valueOf(PieDistroConfigurations.API_VERSION)));
        metrics.addCustomChart(new Metrics.SimplePie("spigot_pie_version", () -> PieDistroConfigurations.DISTRO_VERSION));
        try {
            SpigotPie.loadPlugin(
                    PieEnvironment.builder()
                            .bungeeCord(true)
                            .asDedicatePlugin(false)
                            .filterPackagePath(filterPackagePath)
                            .filterWhitelistMode(filterWhitelistMode)
                            .workFolder(pluginInstance.getDataFolder())
                            .configurationAdapter(BungeeConfigurationAdapter.class)
                            .logger(plugin.getLogger())
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        postLoad();
    }

    @SuppressWarnings("unused")
    public static void inject(Plugin plugin, String... filterPackagePath) {
        inject(plugin,false,filterPackagePath);
    }

    public static void postLoad() {
        CommandManager commandManager = (CommandManager) ObjectManager.getObject(CommandManager.class,"");
        assert commandManager != null;
        for(String commandName : commandManager.getAllCommands()) {
            pluginInstance.getProxy().getPluginManager().registerCommand(pluginInstance, new Command(commandName) {
                @Override
                public void execute(CommandSender sender, String[] args) {
                    CommandTreeNode node = commandManager.getNode(commandName,args);
                    if(node.isRoot()) {
                        TextComponent tc = new TextComponent(PieLang.COMMAND_NOT_FOUND);
                        sender.sendMessage(tc);
                    } else {
                        commandManager.invoke(sender,commandName,args);
                    }
                }
            });
        }
        pluginInstance.getLogger().log(Level.INFO,"[Pie-BC]Command(s) registered.");
        pluginInstance.getLogger().log(Level.INFO,"[Pie-BC]Total: "+commandManager.getAllCommands().length+" command(s)");
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
