package top.jingwenmc.spigotpie.common.configuration;

import java.io.File;
import java.io.IOException;

public abstract class ConfigurationAdapter {
    public abstract Object get(String path);

    public abstract void set(String path,Object value);

    public abstract boolean contains(String path);

    public abstract void init(File file) throws IOException;

    public abstract void load() throws IOException;

    public abstract void save() throws IOException;
}
