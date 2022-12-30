package top.jingwenmc.spigotpie.bungee.command;

import lombok.SneakyThrows;
import top.jingwenmc.spigotpie.common.command.GenericConsumer;
import top.jingwenmc.spigotpie.common.command.PieCommand;
import top.jingwenmc.spigotpie.common.instance.Accepts;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.PreProcessor;
import top.jingwenmc.spigotpie.common.instance.Wire;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

@PieComponent
public class CommandProcessor implements PreProcessor {
    @Wire
    CommandManager commandManager;

    @SneakyThrows
    @Override
    @Accepts(PieCommand.class)
    public void preProcess(Object o, Method m) {
        PieCommand pieCommand = m.getAnnotation(PieCommand.class);
        if(pieCommand.bungeeCord()) {
            ArrayList<String> paths = new ArrayList<>();
            paths.add(pieCommand.value());
            paths.addAll(Arrays.asList(pieCommand.aliases()));
            for(String s : paths) {
                commandManager.addCommandNode(s,new GenericConsumer(o,m));
            }
        }
    }
}
