package top.jingwenmc.spigotpie.spigot.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import top.jingwenmc.spigotpie.common.event.SpigotEventListener;
import top.jingwenmc.spigotpie.common.instance.Accepts;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Platform;
import top.jingwenmc.spigotpie.common.instance.PreProcessor;
import top.jingwenmc.spigotpie.spigot.SpigotPieSpigot;

import java.lang.reflect.Method;

@PieComponent(platform = Platform.SPIGOT)
public class EventProcessor implements PreProcessor {

    @Override
    @Accepts(SpigotEventListener.class)
    public void preProcess(Object o) {
        if(o.getClass().getInterfaces().length!=0 && o.getClass().getInterfaces()[0].equals(Listener.class))
            Bukkit.getPluginManager().registerEvents((Listener)o, SpigotPieSpigot.getPlugin(SpigotPieSpigot.class));
    }

    @Override
    public void preProcess(Object o, Method m) {
        //won't use
    }
}
