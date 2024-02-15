package top.jingwenmc.spigotpie.common.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandItem {

    private CommandSender sender;

    private String[] args;

    /*
        快速判断有没有输入参数
     */
    public boolean isSingle() {
        return args.length == 0;
    }

}
