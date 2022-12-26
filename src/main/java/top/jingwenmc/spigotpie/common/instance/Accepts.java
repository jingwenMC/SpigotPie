package top.jingwenmc.spigotpie.common.instance;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Accepts {
    Class<? extends Annotation> value();

}
