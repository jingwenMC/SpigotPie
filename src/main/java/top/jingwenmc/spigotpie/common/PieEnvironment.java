package top.jingwenmc.spigotpie.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private File workFolder = new File("");
    private Logger logger = null;
}
