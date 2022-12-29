package top.jingwenmc.spigotpie.spigot;

import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import top.jingwenmc.spigotpie.common.PieEnvironment;
import top.jingwenmc.spigotpie.common.SpigotPie;

import java.lang.reflect.Field;

public class SpigotPieSpigot extends JavaPlugin {

    private static JavaPlugin pluginInstance;

    public static void inject(JavaPlugin plugin) {
        pluginInstance = plugin;
        try {
            SpigotPie.loadPlugin(
                    PieEnvironment.builder()
                            .bungeeCord(false)
                            .asDedicatePlugin(false)
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

    }

    @Override
    public void onLoad() {
        pluginInstance = this;
        try {
            SpigotPie.loadPlugin(
                    PieEnvironment.builder()
                            .bungeeCord(false)
                            .asDedicatePlugin(true)
                            .logger(getLogger())
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEnable() {
        try {
            postLoad();
        } catch (Exception e) {
            throw new RuntimeException("Exception during PostLoad:",e);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
