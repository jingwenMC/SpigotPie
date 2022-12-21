package top.jingwenmc.spigotpie.common.command;

import java.lang.annotation.*;

/**
 * Represent a PieCommand
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PieCommand {
    /**
     * Key of command
     * Use space bar to specific sub commands
     */
    String value();

    /**
     * Aliases of the command
     */
    String[] aliases() default "";

    /**
     * Description of the command
     */
    String description() default "";

    /**
     * Permission needed to perform this command
     */
    String permission() default "";
}
