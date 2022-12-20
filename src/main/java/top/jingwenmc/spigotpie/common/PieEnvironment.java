package top.jingwenmc.spigotpie.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.logging.Logger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PieEnvironment {
    private boolean bungeeCord = false;
    private Logger logger = null;

}
