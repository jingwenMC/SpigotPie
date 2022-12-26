package top.jingwenmc.spigotpie.bungee.command;

import top.jingwenmc.spigotpie.common.command.PieCommand;
import top.jingwenmc.spigotpie.common.instance.Accepts;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.PreProcessor;

import java.lang.reflect.Method;

@PieComponent
public class CommandProcessor implements PreProcessor {
    @Override
    @Accepts(PieCommand.class)
    public void preProcess(Object o, Method m) {
        PieCommand pieCommand = m.getAnnotation(PieCommand.class);

    }
}
