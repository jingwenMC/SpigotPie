package top.jingwenmc.spigotpie.common.command;

import java.lang.annotation.*;

/**
 *  Used to identify when a parameter in command is not required
 *  WARNING: if more than one was set, please only use this annotation in the last few params!
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotRequiredCommandParam { //只能在最后几个空添加.
    /**
     * Default value if not present
     * @return the value
     */
    String value() default "";
}
