package top.jingwenmc.spigotpie.common.instance;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
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
    //private static final Map<String,Object> instanceMap = new ConcurrentHashMap<>();
    public static final Map<Field,Object> injectionMap = new ConcurrentHashMap<>();
    private static boolean init = false;

    public static List<Class<?>> scanClassByUrlClassLoader(URLClassLoader cl) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        List<String> filter = getFilter();
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
                            boolean load = true;
                            if(SpigotPie.getEnvironment().isFilterWhitelistMode()) {
                                load = false;
                                for (String f : filter) {
                                    if (name.startsWith(f)) {
                                        load = true;
                                        break;
                                    }
                                }
                            } else {
                                for (String f : filter) {
                                    if (name.startsWith(f)) {
                                        load = false;
                                        break;
                                    }
                                }
                            }
                        if(!load)continue;
                        try {
                            Class<?> clazz = cl.loadClass(name);
                            classes.add(clazz);
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

    @NotNull
    private static List<String> getFilter() {
        List<String> filter = new ArrayList<>(Arrays.asList(SpigotPie.getEnvironment().getFilterPackagePath()));
        if(!SpigotPie.getEnvironment().isFilterWhitelistMode()) {
            if (SpigotPie.getEnvironment().isBungeeCord()) filter.add("top.jingwenmc.spigotpie.spigot");
            else filter.add("top.jingwenmc.spigotpie.bungee");
            filter.add("META-INF");
        } else {
            filter.add("top.jingwenmc.spigotpie.common");
            if (SpigotPie.getEnvironment().isBungeeCord()) filter.add("top.jingwenmc.spigotpie.bungee");
            else filter.add("top.jingwenmc.spigotpie.spigot");
        }
        return filter;
    }

    /**
     * Call on start
     */
    public static void init() throws Exception {
        List<Object> preProcess = new ArrayList<>();
        if(init)return;
        for (Class<?> clazz : scanClassByUrlClassLoader((URLClassLoader) SpigotPie.class.getClassLoader())) {
            if(clazz == null)continue;
            if(clazz.isAnnotationPresent(PieComponent.class)) {
                PieComponent pieComponent = clazz.getDeclaredAnnotation(PieComponent.class);
                if(SpigotPie.getEnvironment().isBungeeCord() && pieComponent.platform().equals(Platform.SPIGOT))continue;
                if(!SpigotPie.getEnvironment().isBungeeCord() && pieComponent.platform().equals(Platform.BUNGEE_CORD))continue;
                String name = clazz.getSimpleName().toLowerCase();
                if(pieComponent.name() != null && !pieComponent.name().isEmpty()) {
                    name = pieComponent.name().toLowerCase();
                }
                if(!SpigotPie.getEnvironment().isBungeeCord() && clazz.getSuperclass().equals(org.bukkit.plugin.java.JavaPlugin.class)) {
                    Class<? extends org.bukkit.plugin.java.JavaPlugin> clazz2 = (Class<? extends org.bukkit.plugin.java.JavaPlugin>) clazz;
                    ObjectManager.addObject(clazz,name,org.bukkit.plugin.java.JavaPlugin.getPlugin(clazz2));
                    preProcess.add(org.bukkit.plugin.java.JavaPlugin.getPlugin(clazz2));
                    continue;
                }
                if(SpigotPie.getEnvironment().isBungeeCord() && clazz.getSuperclass().equals(net.md_5.bungee.api.plugin.Plugin.class)) {
                    Class<? extends net.md_5.bungee.api.plugin.Plugin> clazz2 = (Class<? extends net.md_5.bungee.api.plugin.Plugin>) clazz;
                    for(net.md_5.bungee.api.plugin.Plugin p : net.md_5.bungee.api.ProxyServer.getInstance().getPluginManager().getPlugins()) {
                        if(p.getClass().equals(clazz2)) {
                            ObjectManager.addObject(clazz,name,p);
                            preProcess.add(p);
                            break;
                        }
                    }
                    continue;
                }
                //创建管理实例
                Object o = clazz.getConstructor().newInstance();
                if(!ObjectManager.contains(clazz)) {
                    ObjectManager.addObject(clazz,name,o);
                    preProcess.add(o);
                }
                //因逻辑增多，统一延迟注入字段
                for(Field f : o.getClass().getDeclaredFields()) {
                    if(f.isAnnotationPresent(Wire.class)) injectionMap.put(f,o);
                }
            }
        }
        for(Field f : injectionMap.keySet()) {
            if(f.isAnnotationPresent(Wire.class)) {
                Class<?> required = f.getType();
                if(ObjectManager.contains(required)) {
                    f.setAccessible(true);
                    f.set(injectionMap.get(f),ObjectManager.getObject(required,f.getName()));
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
            for (Object o : preProcess) {
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
                        for (Object o2 : preProcess) {
                            if (o2.getClass().isAnnotationPresent(annoClass)) {
                                m.invoke(o, o2);
                            }
                        }
                    }
                    Method m1 = clazz.getDeclaredMethod("preProcess", Object.class, Method.class);
                    if (m1.isAnnotationPresent(Accepts.class)) {
                        Class<? extends Annotation> annoClass = m1.getDeclaredAnnotation(Accepts.class).value();
                        ElementType[] types = annoClass.getDeclaredAnnotation(Target.class).value();
                        if (types.length > 1) throw new MultiTargetException("Only one target is supported");
                        ElementType type = types[0];
                        if (!type.equals(ElementType.METHOD))
                            throw new UnsupportedTargetException("Expecting ElementType.METHOD, got " + type);
                        for (Object o2 : preProcess) {
                            for (Method m2 : o2.getClass().getMethods()) {
                                if (m2.isAnnotationPresent(annoClass)) {
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
    }

    /**
     * Get declared instance
     * @param name Class name
     * @return The instance. Not found -> null.
     * @deprecated using new management system
     */
    @SneakyThrows
    @Deprecated
    @Nullable
    public static Object getDeclaredInstance(String name) {
        return ObjectManager.getObject(Class.forName(name),"");
    }

    /**
     * Get declared instance
     * @param clazz Class
     * @return The instance. Not found -> null.
     * @deprecated using new management system
     */
    @Nullable
    @Deprecated
    public static Object getDeclaredInstance(Class<?> clazz) {
        return ObjectManager.getObject(clazz,"");
    }
}
