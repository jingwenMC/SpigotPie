package top.jingwenmc.spigotpie.common.configuration;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.io.File;
import java.lang.reflect.Field;

public abstract class BaseConfiguration {
    private File file;
    private CommentedFileConfig fileConfig;
    public void reloadConfig() throws IllegalAccessException {
        for(Field f : this.getClass().getDeclaredFields()) {
            if(f.isAnnotationPresent(Configuration.class)) {
                f.setAccessible(true);
                Configuration configuration = f.getDeclaredAnnotation(Configuration.class);
                String path = configuration.value();
                if(fileConfig.contains(path)) f.set(this,fileConfig.get(path));
            }
        }
        saveConfig();
    }

    public void saveConfig() throws IllegalAccessException {
        for(Field f : this.getClass().getDeclaredFields()) {
            if(f.isAnnotationPresent(Configuration.class)) {
                f.setAccessible(true);
                Configuration configuration = f.getDeclaredAnnotation(Configuration.class);
                String path = configuration.value();
                String comment = configuration.comment();
                if(fileConfig.contains(path)) {
                    if(!f.get(this).equals(fileConfig.get(path))) {
                        fileConfig.set(path,f.get(this));
                    }
                } else {
                    fileConfig.set(path,f.get(this));
                    if(comment != null && !comment.isEmpty()) fileConfig.setComment(path,comment);
                }
            }
        }
        fileConfig.save();
    }
}
