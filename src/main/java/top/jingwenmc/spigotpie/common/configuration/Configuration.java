package top.jingwenmc.spigotpie.common.configuration;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Configuration {
    String value();

}
