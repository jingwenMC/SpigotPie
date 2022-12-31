package top.jingwenmc.spigotpie.common.configuration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigurationFile {
    String value();
}
