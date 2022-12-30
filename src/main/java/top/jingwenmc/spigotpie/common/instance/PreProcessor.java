package top.jingwenmc.spigotpie.common.instance;

import java.lang.reflect.Method;

public interface PreProcessor {
    default void preProcess(Object o){}

    default void preProcess(Object o, Method m){}
}
