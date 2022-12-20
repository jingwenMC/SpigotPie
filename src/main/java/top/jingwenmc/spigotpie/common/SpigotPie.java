package top.jingwenmc.spigotpie.common;

import lombok.Getter;

public class SpigotPie {
    @Getter
    private PieEnvironment environment;


    public static SpigotPie loadPlugin(PieEnvironment environment) {
        SpigotPie pie = new SpigotPie();
        pie.environment = environment;
        //start pie
        return pie;
    }
}
