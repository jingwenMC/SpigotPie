package top.jingwenmc.spigotpie.common.instance;

import java.lang.reflect.Method;

public interface PreProcessor {
    void preProcess(Object o);

    void preProcess(Object o, Method m);
}
