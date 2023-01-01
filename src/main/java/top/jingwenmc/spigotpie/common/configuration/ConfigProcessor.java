package top.jingwenmc.spigotpie.common.configuration;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
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
            File configFile = new File(SpigotPie.getEnvironment().getWorkFolder(), configurationFile.value());
            f1.set(o,configFile);
            try (CommentedFileConfig fileConfig = CommentedFileConfig.builder(configFile)
                    .onFileNotFound(FileNotFoundAction.CREATE_EMPTY)
                    .build()) {
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
