package top.jingwenmc.spigotpie.common.command;

public interface BaseCommandContainer {
    void noPermission(CommandSender sender,String... args);

    void notFound(CommandSender sender,String... args);
}
