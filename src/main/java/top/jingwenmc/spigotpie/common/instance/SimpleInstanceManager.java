package top.jingwenmc.spigotpie.common.instance;

import org.jetbrains.annotations.Nullable;
import top.jingwenmc.spigotpie.common.SpigotPie;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@SuppressWarnings("unchecked")
public class SimpleInstanceManager {
    private static final Map<String,Object> instanceMap = new ConcurrentHashMap<>();
    private static final Map<String,Class<?>> classMap = new ConcurrentHashMap<>();
    public static final Map<Field,Object> injectionMap = new ConcurrentHashMap<>();
    private static boolean init = false;

    public static List<Class<?>> scanClassByUrlClassLoader(URLClassLoader cl) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        for(URL url : cl.getURLs()) {
            if(url.getPath().endsWith(".jar"))
                try(JarFile jarFile = new JarFile(url.getPath())){
                    Enumeration<?> enumeration = jarFile.entries();
                    while (enumeration.hasMoreElements()) {
                        JarEntry entry = (JarEntry) enumeration.nextElement();
                        String name = entry.getName();
                        if(name.endsWith(".class")) {
                            name = name.substring(0,name.length()-6);
                            name = name.replace('/','.').replace('\\','.');
                            try {
                                Class<?> clazz = cl.loadClass(name);
                                classes.add(clazz);
                                classMap.put(clazz.getName(),clazz);
                            }catch (ClassNotFoundException | NoClassDefFoundError e) {
                                System.err.println("Class Not Found: "+name);
                                System.err.println("Won't create instance for it.");
                            }

                        }
                    }
                }
        }
        return classes;
    }

    /**
     * Call on start
     */
    public static void init() throws Exception {
        if(init)return;
        for (Class<?> clazz : scanClassByUrlClassLoader((URLClassLoader) SpigotPie.class.getClassLoader())) {
            if(clazz == null)continue;
            if(clazz.isAnnotationPresent(PieComponent.class)) {
                if(!SpigotPie.getEnvironment().isBungeeCord() && clazz.getSuperclass().equals(org.bukkit.plugin.java.JavaPlugin.class)) {
                    Class<? extends org.bukkit.plugin.java.JavaPlugin> clazz2 = (Class<? extends org.bukkit.plugin.java.JavaPlugin>) clazz;
                    instanceMap.put(clazz.getName(), org.bukkit.plugin.java.JavaPlugin.getPlugin(clazz2));
                }
                if(SpigotPie.getEnvironment().isBungeeCord() && clazz.getSuperclass().equals(net.md_5.bungee.api.plugin.Plugin.class)) {
                    Class<? extends net.md_5.bungee.api.plugin.Plugin> clazz2 = (Class<? extends net.md_5.bungee.api.plugin.Plugin>) clazz;
                    for(net.md_5.bungee.api.plugin.Plugin p : net.md_5.bungee.api.ProxyServer.getInstance().getPluginManager().getPlugins()) {
                        if(p.getClass().equals(clazz2)) instanceMap.put(clazz.getName(), p);
                    }
                }
                //管理实例
                Object o = !instanceMap.containsKey(clazz.getName()) ? clazz.getConstructor().newInstance():instanceMap.get(clazz.getName());
                if(!instanceMap.containsKey(clazz.getName()))instanceMap.put(clazz.getName(),o);
                //直接注入字段
                for(Field f : o.getClass().getDeclaredFields()) {
                    if(f.isAnnotationPresent(Wire.class)) {
                        String required = f.getType().getName();
                        if(instanceMap.containsKey(required)) {
                            f.setAccessible(true);
                            f.set(o,instanceMap.get(required));
                        } else {
                            injectionMap.put(f,o);
                        }
                    }
                }
            }
        }
        for(Field f : injectionMap.keySet()) {
            if(f.isAnnotationPresent(Wire.class)) {
                String required = f.getType().getName();
                if(instanceMap.containsKey(required)) {
                    f.setAccessible(true);
                    f.set(injectionMap.get(f),instanceMap.get(required));
                    injectionMap.remove(f);
                }
            }
        }
        if(!injectionMap.isEmpty()) {
            System.err.println("Exception during Load:");
            System.err.println("===============[Spigot Pie - Warning]===============");
            System.err.println("[警告] 仍有以下声明的字段没有得到注入");
            System.err.println("[WARN] Field didn't inject:");
            for(Field f : injectionMap.keySet()) {
                System.err.println("[Field]{"+f.getName()+"},[Require]{"+injectionMap.get(f)+"};");
            }
            System.err.println("[警告] 可能是因为尝试Wire不受管理的Class");
            System.err.println("[WARN] Maybe tried to inject unmanaged class");
            System.err.println("[警告] 将会保留默认值");
            System.err.println("[WARN] Will stay default");
            System.err.println("===============[Spigot Pie - Warning]===============");
        }

        //PreProcessor
        try {
            for (Object o : instanceMap.values()) {
                Class<?> clazz = o.getClass();
                if (Arrays.asList(clazz.getInterfaces()).contains(PreProcessor.class)) {
                    Method m = clazz.getDeclaredMethod("preProcess", Object.class);
                    if (m.isAnnotationPresent(Accepts.class)) {
                        Class<? extends Annotation> annoClass = m.getDeclaredAnnotation(Accepts.class).value();
                        ElementType[] types = annoClass.getDeclaredAnnotation(Target.class).value();
                        if (types.length > 1) throw new MultiTargetException("Only one target is supported");
                        ElementType type = types[0];
                        if (!type.equals(ElementType.TYPE))
                            throw new UnsupportedTargetException("Expecting ElementType.TYPE, got " + type);
                        for (Object o2 : instanceMap.values()) {
                            if (o2.getClass().isAnnotationPresent(annoClass)) {
                                m.invoke(o, o2);
                            }
                        }
                    }
                    Method m1 = clazz.getDeclaredMethod("preProcess", Object.class, Method.class);
                    if (m1.isAnnotationPresent(Accepts.class)) {
                        Class<? extends Annotation> annoClass = m.getDeclaredAnnotation(Accepts.class).value();
                        ElementType[] types = annoClass.getDeclaredAnnotation(Target.class).value();
                        if (types.length > 1) throw new MultiTargetException("Only one target is supported");
                        ElementType type = types[0];
                        if (!type.equals(ElementType.METHOD))
                            throw new UnsupportedTargetException("Expecting ElementType.METHOD, got " + type);
                        for (Object o2 : instanceMap.values()) {
                            for (Method m2 : o2.getClass().getMethods()) {
                                if (m2.getClass().isAnnotationPresent(annoClass)) {
                                    m1.invoke(o, o2, m2);
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e) {
            throw new RuntimeException("Exception during PreProcess: ",e);
        }
        init = true;
        for(String s : instanceMap.keySet()) {
            System.out.println("Loaded: "+s);
        }
    }

    /**
     * Get declared instance
     * @param name Class name
     * @return The instance. Not found -> null.
     */
    @Nullable
    public static Object getDeclaredInstance(String name) {
        return instanceMap.get(name);
    }

    /**
     * Get declared instance
     * @param clazz Class
     * @return The instance. Not found -> null.
     */
    @Nullable
    public static Object getDeclaredInstance(Class<?> clazz) {
        return instanceMap.get(clazz.getName());
    }
}
