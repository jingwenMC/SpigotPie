package top.jingwenmc.spigotpie.bungee.command;

import lombok.SneakyThrows;
import top.jingwenmc.spigotpie.common.command.GenericConsumer;
import top.jingwenmc.spigotpie.common.command.PieCommand;
import top.jingwenmc.spigotpie.common.instance.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

@PieComponent(platform = Platform.BUNGEE_CORD)
public class CommandProcessor implements PreProcessor {
    @Wire
    CommandManager commandManager;

    @Override
    public void preProcess(Object o) {
        //Didn't use this type of processor
    }

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
                if(s.isEmpty())continue;
                if(pieCommand.helpCommand()) commandManager.addCommandNode(s,null,pieCommand);
                else commandManager.addCommandNode(s,new GenericConsumer(pieCommand,o,m),pieCommand);
            }
        }
    }
}
