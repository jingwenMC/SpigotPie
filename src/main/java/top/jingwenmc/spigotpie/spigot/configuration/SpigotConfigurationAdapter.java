package top.jingwenmc.spigotpie.spigot.configuration;

import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import top.jingwenmc.spigotpie.common.configuration.ConfigurationAdapter;

import java.io.File;
import java.io.IOException;

public class SpigotConfigurationAdapter extends ConfigurationAdapter {
    YamlConfiguration yamlConfiguration;
    File file;

    @Override
    public Object get(String path) {
        return yamlConfiguration.get(path);
    }

    @Override
    public void set(String path, Object value) {
        yamlConfiguration.set(path, value);
    }

    @Override
    public boolean contains(String path) {
        return yamlConfiguration.contains(path);
    }

    @Override
    public void init(File file) {
        this.file = file;
        load();
    }

    @SneakyThrows
    @Override
    public void load() {
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void save() throws IOException {
        yamlConfiguration.save(file);
    }
}
