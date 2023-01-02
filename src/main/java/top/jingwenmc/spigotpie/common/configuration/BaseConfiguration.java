package top.jingwenmc.spigotpie.common.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public abstract class BaseConfiguration {
    private File file;
    private ConfigurationAdapter fileConfig;
    public void reloadConfig() throws IllegalAccessException, IOException {
        for(Field f : this.getClass().getDeclaredFields()) {
            if(f.isAnnotationPresent(Configuration.class)) {
                f.setAccessible(true);
                Configuration configuration = f.getDeclaredAnnotation(Configuration.class);
                String path = configuration.value();
                if(fileConfig.contains(path)) {
                    f.set(this,fileConfig.get(path));
                }
            }
        }
        saveConfig();
    }

    public void saveConfig() throws IllegalAccessException, IOException {
        for(Field f : this.getClass().getDeclaredFields()) {
            if(f.isAnnotationPresent(Configuration.class)) {
                f.setAccessible(true);
                Configuration configuration = f.getDeclaredAnnotation(Configuration.class);
                String path = configuration.value();
                if(fileConfig.contains(path)) {
                    if(!f.get(this).equals(fileConfig.get(path))) {
                        fileConfig.set(path,f.get(this));
                    }
                } else {
                    fileConfig.set(path,f.get(this));
                }
            }
        }
        fileConfig.save();
    }
}
