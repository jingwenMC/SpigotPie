package top.jingwenmc.spigotpie.common.lang;

import net.md_5.bungee.api.ChatColor;
import top.jingwenmc.spigotpie.common.configuration.BaseConfiguration;
import top.jingwenmc.spigotpie.common.configuration.Configuration;
import top.jingwenmc.spigotpie.common.configuration.ConfigurationFile;
import top.jingwenmc.spigotpie.common.instance.PieComponent;

@PieComponent
@ConfigurationFile("SpigotPie/lang.yml")
public class PieLang extends BaseConfiguration {
    @Configuration("too_few_args")
    public static String TOO_FEW_ARGS = ChatColor.RED+"指令的参数不足！至少需要$1个参数！";

    @Configuration("too_many_args")
    public static String TOO_MANY_ARGS = ChatColor.RED+"指令的参数过多！最多需要$1个参数！";

    @Configuration("command_not_found")
    public static String COMMAND_NOT_FOUND = ChatColor.RED+"指令未找到！";

    @Configuration("command_error")
    public static String COMMAND_ERROR = ChatColor.RED+"指令发生错误！请查看控制台！";

    @Configuration("no_perm")
    public static String NO_PERM = ChatColor.RED+"权限不足!请联系管理员!";

    @Configuration("command_fallback")
    public static String COMMAND_FALLBACK = ChatColor.RED+"子指令未找到!请检查输入项目是否正确!";

    @Configuration("help_title")
    public static String HELP_TITLE = ChatColor.AQUA+"==========[子指令列表]==========";

    @Configuration("help_content")
    public static String HELP_CONTENT = ChatColor.WHITE+"   [$1] - $2";
}
