package top.jingwenmc.spigotpie.common.lang;

import net.md_5.bungee.api.ChatColor;
import top.jingwenmc.spigotpie.common.configuration.BaseConfiguration;
import top.jingwenmc.spigotpie.common.configuration.Configuration;
import top.jingwenmc.spigotpie.common.configuration.ConfigurationFile;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

@PieComponent
@ConfigurationFile("pie_lang.toml")
public class PieLang extends BaseConfiguration {
    @Configuration("too_few_args")
    public static String TOO_FEW_ARGS = ChatColor.RED+"指令的参数不足！至少需要$1个参数！";

    @Configuration("too_many_args")
    public static String TOO_MANY_ARGS = ChatColor.RED+"指令的参数过多！最多需要$1个参数！";

    @Configuration("command_not_found")
    public static String COMMAND_NOT_FOUND = ChatColor.RED+"指令未找到！";
}
