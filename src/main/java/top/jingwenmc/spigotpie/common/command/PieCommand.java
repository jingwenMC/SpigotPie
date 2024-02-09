package top.jingwenmc.spigotpie.common.command;

import java.lang.annotation.*;

/**
 * Represent a PieCommand
 */
@Target(ElementType.METHOD)
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

    /**
     * true - will work on BungeeCord
     * false - will not work on BungeeCord
     */
    boolean bungeeCord();

    /**
     * true - will work on Spigot
     * false - will not work on Spigot
     */
    boolean spigot();

    /**
     * Custom message handler
     * please input a {@link top.jingwenmc.spigotpie.common.instance.PieComponent} name which implements {@link top.jingwenmc.spigotpie.common.command.message.CommandMessageHandler}
     */
    String messageHandler() default "default";

    /**
     * Define the command as help command
     * when this is true, everything in the method won't work
     */
    boolean helpCommand() default false;
}
