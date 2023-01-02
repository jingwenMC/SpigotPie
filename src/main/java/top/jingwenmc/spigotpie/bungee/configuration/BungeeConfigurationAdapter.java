package top.jingwenmc.spigotpie.bungee.configuration;

import lombok.SneakyThrows;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import top.jingwenmc.spigotpie.common.configuration.ConfigurationAdapter;

import java.io.File;
import java.io.IOException;

public class BungeeConfigurationAdapter extends ConfigurationAdapter {
    Configuration configuration;
    File file;

    @Override
    public Object get(String path) {
        return configuration.get(path);
    }

    @Override
    public void set(String path, Object value) {
        configuration.set(path, value);
    }

    @Override
    public boolean contains(String path) {
        return configuration.contains(path);
    }

    @Override
    public void init(File file) {
        this.file = file;
        load();
    }

    @SneakyThrows
    @Override
    public void load() {
        configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
    }

    @Override
    public void save() throws IOException {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration,file);
    }
}
