package top.jingwenmc.spigotpie.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import top.jingwenmc.spigotpie.common.PieEnvironment;
import top.jingwenmc.spigotpie.common.SpigotPie;

public final class SpigotPieBungee extends Plugin {

    public static Plugin pluginInstance = null;

    public static void inject(Plugin plugin) {
        pluginInstance = plugin;
        try {
            SpigotPie.loadPlugin(
                    PieEnvironment.builder()
                            .bungeeCord(true)
                            .asDedicatePlugin(false)
                            .logger(plugin.getLogger())
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onLoad() {
        pluginInstance = this;
        try {
            SpigotPie.loadPlugin(
                    PieEnvironment.builder()
                            .bungeeCord(true)
                            .asDedicatePlugin(true)
                            .logger(getLogger())
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Plugin startup logic
    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
