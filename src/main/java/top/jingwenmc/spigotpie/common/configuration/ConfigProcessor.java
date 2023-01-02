package top.jingwenmc.spigotpie.common.configuration;

import lombok.SneakyThrows;
import top.jingwenmc.spigotpie.common.SpigotPie;
import top.jingwenmc.spigotpie.common.instance.Accepts;
import top.jingwenmc.spigotpie.common.instance.PieComponent;
import top.jingwenmc.spigotpie.common.instance.PreProcessor;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@PieComponent
public class ConfigProcessor implements PreProcessor {
    @SneakyThrows
    @Override
    @Accepts(ConfigurationFile.class)
    public void preProcess(Object o) {
        if(o.getClass().getSuperclass().equals(BaseConfiguration.class)) {
            ConfigurationFile configurationFile = o.getClass().getDeclaredAnnotation(ConfigurationFile.class);
            Field f1 = o.getClass().getSuperclass().getDeclaredField("file");
            f1.setAccessible(true);
            Field f2 = o.getClass().getSuperclass().getDeclaredField("fileConfig");
            f2.setAccessible(true);
            SpigotPie.getEnvironment().getWorkFolder().mkdirs();
            String[] strings = configurationFile.value().split("/");
            File folder = SpigotPie.getEnvironment().getWorkFolder();
            for (int i = 0;i<strings.length-1;i++) {
                folder = new File(folder,strings[i]);
                folder.mkdirs();
            }
            File configFile = new File(folder, strings[strings.length-1]);
            f1.set(o,configFile);
            try{
                ConfigurationAdapter fileConfig = SpigotPie.getEnvironment().getConfigurationAdapter().newInstance();
                fileConfig.init(configFile);
                f2.set(o,fileConfig);
                ((BaseConfiguration) o).reloadConfig();
            } catch (Exception e) {
                throw new RuntimeException("Exception during config file load:",e);
            }
        }
    }

    @Override
    public void preProcess(Object o, Method m) {
        //Didn't use this type of processor
    }
}
