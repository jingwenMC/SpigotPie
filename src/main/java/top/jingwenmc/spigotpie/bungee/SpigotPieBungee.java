package top.jingwenmc.spigotpie.bungee;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import top.jingwenmc.spigotpie.common.PieEnvironment;
import top.jingwenmc.spigotpie.common.SpigotPie;

public final class SpigotPieBungee extends Plugin {
    @Getter
    private static SpigotPie instance;

    @Override
    public void onEnable() {
        instance = SpigotPie.loadPlugin(
                PieEnvironment.builder()
                        .bungeeCord(true)
                        .logger(getLogger())
                        .build());
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
