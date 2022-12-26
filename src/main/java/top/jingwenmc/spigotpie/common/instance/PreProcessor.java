package top.jingwenmc.spigotpie.common.instance;

import java.lang.reflect.Method;

public interface PreProcessor {
    default void process(Object o) {}

    default void process(Object o, Method m) {}
}
