package top.jingwenmc.spigotpie.common.instance;

import java.lang.annotation.*;

/**
 * Represent a PieComponent to register into the instance manager on load
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PieComponent {
}
