package top.jingwenmc.spigotpie.bungee.event;

import net.md_5.bungee.api.plugin.Listener;
import top.jingwenmc.spigotpie.bungee.SpigotPieBungee;
import top.jingwenmc.spigotpie.common.event.BungeeEventListener;
import top.jingwenmc.spigotpie.common.instance.Accepts;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.Platform;
import top.jingwenmc.spigotpie.common.instance.PreProcessor;

import java.lang.reflect.Method;

@PieComponent(platform = Platform.BUNGEE_CORD)
public class EventProcessor implements PreProcessor {
    @Override
    @Accepts(BungeeEventListener.class)
    public void preProcess(Object o) {
        if(o.getClass().getInterfaces().length!=0 && o.getClass().getInterfaces()[0].equals(Listener.class))
            SpigotPieBungee.getPluginInstance().getProxy().getPluginManager().registerListener(SpigotPieBungee.getPluginInstance(),(Listener) o);
    }

    @Override
    public void preProcess(Object o, Method m) {

    }
}
