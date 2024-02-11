package top.jingwenmc.spigotpie.common;

import lombok.Getter;
import top.jingwenmc.spigotpie.PieDistroConfigurations;
import top.jingwenmc.spigotpie.common.instance.SimpleInstanceManager;
import top.jingwenmc.spigotpie.common.lang.PieLang;

import java.util.logging.Level;

public class SpigotPie {
    @Getter
    private static PieEnvironment environment;

    public static void loadPlugin(PieEnvironment environment) {
        SpigotPie.environment = environment;
        environment.getLogger().info("Language Setting: " + PieLang.LANGUAGE_NAME);
        environment.getLogger().info("You can change this in SpigotPie/Lang.yml");
        environment.getLogger().info(PieLang.LOADING);
        char[] packageName = new char[]{'t','o','p','.','j','i','n','g','w','e','n','m','c','.','s','p','i','g','o','t','p','i','e','.','c','o','m','m','o','n'};
        if(!environment.isAsDedicatePlugin() && SpigotPie.class.getName().startsWith(new String(packageName))) {
            environment.getLogger().log(Level.SEVERE,PieLang.FAILED_TO_LOAD);
            environment.getLogger().log(Level.SEVERE,"Reason: " + PieLang.PACKAGE_CONFLICT);
            throw new IllegalStateException("Exception during PreLoad: Developer notice: ensure to use relocation while not using plugin mode!");
        }

        //start pie
        environment.getLogger().info(PieLang.LOADING_INSTANCE);
        try {
            SimpleInstanceManager.init();
            environment.getLogger().info(PieLang.LOADING_INSTANCE_COMPLETE);
            environment.getLogger().info(
                      "                         \n" +
                            "     _              _    \n" +
                            "    /_`_  ._  _ _/_/_/._ \n" +
                            "     \\__ \\/ __ \\/ / __ `/ __ \\/ __/  / /_/ / / _ \\\n" +
                            "   ._//_///_//_// /  //_'\n" +
                            "    /    _/      Version:" + PieDistroConfigurations.DISTRO_VERSION + "\n" +
                            "                         ");
            environment.getLogger().info(PieLang.LOADING_COMPLETE);
        } catch (Exception e) {
            environment.getLogger().log(Level.SEVERE,PieLang.FAILED_TO_LOAD);
            environment.getLogger().log(Level.SEVERE,PieLang.CHECK_CAUSE);
            throw new RuntimeException("Exception during Load: Unknown Exception",e);
        }
    }
}
