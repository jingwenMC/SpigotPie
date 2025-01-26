package top.jingwenmc.spigotpie.spigot;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import top.jingwenmc.spigotpie.PieDistroConfigurations;
import top.jingwenmc.spigotpie.common.instance.ObjectManager;
import top.jingwenmc.spigotpie.common.lang.PieLang;
import top.jingwenmc.spigotpie.spigot.command.CommandManager;
import top.jingwenmc.spigotpie.common.PieEnvironment;
import top.jingwenmc.spigotpie.common.SpigotPie;
import top.jingwenmc.spigotpie.common.command.CommandTreeNode;
import top.jingwenmc.spigotpie.spigot.configuration.SpigotConfigurationAdapter;
import top.jingwenmc.spigotpie.spigot.metrics.Metrics;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class SpigotPieSpigot extends JavaPlugin {
    @Getter
    private static JavaPlugin pluginInstance;
    @Getter
    private static Metrics metrics;

    public static void inject(JavaPlugin plugin,boolean filterWhitelistMode,String... filterPackagePath) {
        pluginInstance = plugin;
        metrics = new Metrics(pluginInstance,17703);
        metrics.addCustomChart(new Metrics.SimplePie("spigot_pie_api_version", () -> String.valueOf(PieDistroConfigurations.API_VERSION)));
        metrics.addCustomChart(new Metrics.SimplePie("spigot_pie_version", () -> PieDistroConfigurations.DISTRO_VERSION));
        try {
            SpigotPie.loadPlugin(
                    PieEnvironment.builder()
                            .bungeeCord(false)
                            .asDedicatePlugin(false)
                            .filterPackagePath(filterPackagePath)
                            .filterWhitelistMode(filterWhitelistMode)
                            .workFolder(pluginInstance.getDataFolder())
                            .configurationAdapter(SpigotConfigurationAdapter.class)
                            .logger(plugin.getLogger())
                            .build());
            postLoad();
        } catch (Exception e) {
            throw new RuntimeException("Exception during PostLoad:",e);
        }
    }


    @SuppressWarnings("unused")
    public static void inject(JavaPlugin plugin, String... filterPackagePath) {
        inject(plugin,false,filterPackagePath);
    }

    public static void postLoad() throws NoSuchFieldException, IllegalAccessException {
        SimpleCommandMap commandMap;
        SimplePluginManager pluginManager = (SimplePluginManager) pluginInstance.getServer().getPluginManager();
        Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);
        CommandManager commandManager = (CommandManager) ObjectManager.getObject(CommandManager.class,"");
        assert commandManager != null;
        for(String commandName : commandManager.getAllCommands()) {
            commandMap.register("pie_"+pluginInstance.getName().toLowerCase(), new Command(commandName) {
                @Override
                public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                    CommandTreeNode node = commandManager.getNode(commandName,args);
                    if(node.isRoot()) {
                        sender.sendMessage(PieLang.COMMAND_NOT_FOUND);
                    } else {
                        commandManager.invoke(sender,commandName,args);
                    }
                    return true;
                }

                @Override
                public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
                    System.out.println(args);
                    System.out.println(alias);
                    if(args.length<2) {
                        return super.tabComplete(sender, alias, args);
                    }
                    CommandTreeNode node = commandManager.getNode(commandName, Arrays.copyOfRange(args, 0, args.length - 1));
                    if((!node.isRoot()) && node.getPath().equalsIgnoreCase(args[args.length - 2])) {
                        return Arrays.asList(node.getTreeMap().keySet().toArray(new String[0]));
                    }
                    return super.tabComplete(sender, alias, args);
                }
            });
        }
        pluginInstance.getLogger().log(Level.INFO,"[Pie]Command(s) registered.");
        pluginInstance.getLogger().log(Level.INFO,"[Pie]Total: "+commandManager.getAllCommands().length+" command(s)");
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
