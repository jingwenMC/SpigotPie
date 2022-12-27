package top.jingwenmc.spigotpie.common.command;

import java.lang.annotation.*;

/**
 *  Used to identify when a parameter in command is not required
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotRequiredCommandParam { //只能在最后几个空添加
    String defaultValue() default "";
}
