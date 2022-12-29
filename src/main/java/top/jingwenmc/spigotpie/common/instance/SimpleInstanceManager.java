package top.jingwenmc.spigotpie.common.instance;

import org.jetbrains.annotations.Nullable;
import top.jingwenmc.spigotpie.common.SpigotPie;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleInstanceManager {
    private static final Map<String,Object> instanceMap = new ConcurrentHashMap<>();
    public static final Map<Field,Object> injectionMap = new ConcurrentHashMap<>();
    private static boolean init = false;
    public static List<Class<?>> scanClassByClassLoader(ClassLoader cl) throws IOException {
        Enumeration<URL> urlEnumeration = cl.getResources("");
        List<Class<?>> classes = new ArrayList<>();
        while (urlEnumeration.hasMoreElements()) {
            URL url = urlEnumeration.nextElement();
            if (url.getProtocol().equals("file")) {
                loadClassByPath(null, url.getPath(), classes, cl);
            }
        }
        return classes;
    }

    private static void loadClassByPath(String root, String path, List<Class<?>> list, ClassLoader load) {
        File f = new File(path);
        if(root==null) root = f.getPath();
        if (f.isFile() && f.getName().matches("^.*\\.class$")) {
            try {
                String classPath = f.getPath();
                String className = classPath.substring(root.length()+1,classPath.length()-6).replace('/','.').replace('\\','.');
                list.add(load.loadClass(className));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            File[] fs = f.listFiles();
            if (fs == null) return;
            for (File file : fs) {
                loadClassByPath(root,file.getPath(), list, load);
            }
        }
    }

    /**
     * Call on start
     */
    public static void init() throws Exception {
        if(init)return;
        for (Class<?> clazz : scanClassByClassLoader(SpigotPie.class.getClassLoader())) {
            if(clazz == null)continue;
            if(clazz.isAnnotationPresent(PieComponent.class)) {
                if(!SpigotPie.getEnvironment().isBungeeCord() && clazz.getSuperclass().equals(org.bukkit.plugin.java.JavaPlugin.class)) {
                    Class<? extends org.bukkit.plugin.java.JavaPlugin> clazz2 = (Class<? extends org.bukkit.plugin.java.JavaPlugin>) clazz;
                    instanceMap.put(clazz.getName(), org.bukkit.plugin.java.JavaPlugin.getPlugin(clazz2));
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
        for(Object o : instanceMap.values()) {
            Class<?> clazz = o.getClass();
            if (Arrays.asList(clazz.getInterfaces()).contains(PreProcessor.class)) {
                Method m = clazz.getDeclaredMethod("process",Object.class);
                if(m.isAnnotationPresent(Accepts.class)) {
                    Class<? extends Annotation> annoClass = m.getAnnotation(Accepts.class).value();
                    ElementType[] types = annoClass.getAnnotation(Target.class).value();
                    if(types.length>1)throw new MultiTargetException("Only one target is supported");
                    ElementType type = types[0];
                    if(!type.equals(ElementType.TYPE))throw new UnsupportedTargetException("Exception during PreProcess: Expecting ElementType.TYPE, got "+type);
                    for(Object o2 : instanceMap.values()) {
                        if(o2.getClass().isAnnotationPresent(annoClass)) {
                            m.invoke(o,o2);
                        }
                    }
                }
                Method m1 = clazz.getDeclaredMethod("process",Object.class,Method.class);
                if(m1.isAnnotationPresent(Accepts.class)) {
                    Class<? extends Annotation> annoClass = m.getAnnotation(Accepts.class).value();
                    ElementType[] types = annoClass.getAnnotation(Target.class).value();
                    if(types.length>1)throw new MultiTargetException("Only one target is supported");
                    ElementType type = types[0];
                    if(!type.equals(ElementType.METHOD))throw new UnsupportedTargetException("Exception during PreProcess: Expecting ElementType.METHOD, got "+type);
                    for(Object o2 : instanceMap.values()) {
                        for(Method m2 : o2.getClass().getMethods()) {
                            if(m2.getClass().isAnnotationPresent(annoClass)) {
                                m1.invoke(o,o2,m2);
                            }
                        }
                    }
                }
            }
        }
        init = true;
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
