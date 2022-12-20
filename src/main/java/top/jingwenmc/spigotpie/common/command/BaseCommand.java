package top.jingwenmc.spigotpie.common.command;

import java.lang.annotation.*;

/**
 * Represent a BaseCommand
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BaseCommand {
    /**
     * Key of command
     */
    String value();

    /**
     * Aliases of the command
     */
    String[] aliases();

    /**
     * Description of the command
     */
    String description();

    /**
     * Permission needed to perform this command
     */
    String permission();
}
