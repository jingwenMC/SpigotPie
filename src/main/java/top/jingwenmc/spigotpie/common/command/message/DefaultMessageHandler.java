package top.jingwenmc.spigotpie.common.command.message;

import top.jingwenmc.spigotpie.common.command.CommandItem;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.lang.PieLang;

import java.util.Map;

@PieComponent(name = "default")
public class DefaultMessageHandler implements CommandMessageHandler{
    @Override
    public void handleMessage(CommandItem item, MessageType type, Map<String,String> parameters) {
        switch (type) {
            case NO_PERM:
                item.getSender().sendMessage(replaceAll(PieLang.NO_PERM,parameters));
                break;
            case TOO_FEW_ARGS:
                item.getSender().sendMessage(replaceAll(PieLang.TOO_FEW_ARGS,parameters));
                break;
            case TOO_MANY_ARGS:
                item.getSender().sendMessage(replaceAll(PieLang.TOO_MANY_ARGS,parameters));
                break;
            case HELP_PAGE:
                item.getSender().sendMessage(PieLang.HELP_TITLE);
                for(String cmd : parameters.keySet()) {
                    String description = parameters.get(cmd);
                    item.getSender().sendMessage(PieLang.HELP_CONTENT.replace("$1",cmd).replace("$2",description));
                }
                break;
            default:
                item.getSender().sendMessage(PieLang.COMMAND_ERROR);
        }
    }

    private String replaceAll(String raw,Map<String,String> parameters) {
        String r = raw;
        for(String s : parameters.keySet()) {
            r = r.replace(s, parameters.get(s));
        }
        return r;
    }
}
