package top.jingwenmc.spigotpie.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.jingwenmc.spigotpie.common.configuration.ConfigurationAdapter;

import java.io.File;
import java.util.logging.Logger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PieEnvironment {
    private boolean bungeeCord = false;
    private boolean asDedicatePlugin = false;
    private String[] filterPackagePath = null;
    private boolean filterWhitelistMode = false;
    private File workFolder = new File("");
    private Class<? extends ConfigurationAdapter> configurationAdapter = null;
    private Logger logger = null;
}
