package top.jingwenmc.spigotpie.common.instance;

import org.jetbrains.annotations.Nullable;
import top.jingwenmc.spigotpie.common.SpigotPie;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleInstanceManager {
    private static final Map<String,Object> instanceMap = new ConcurrentHashMap<>();
    private static boolean init = false;
    public static List<Class<?>> scanClassByClassLoader(ClassLoader cl) throws IOException {
        Enumeration<URL> urlEnumeration = cl.getResources("");
        List<Class<?>> classes = new ArrayList<>();
        while (urlEnumeration.hasMoreElements()) {
            URL url = urlEnumeration.nextElement();
            //文件类型（其实是文件夹）
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

    public static void injectInstances(Class<?> required,@Nullable Class<?> from) throws Exception {
        if(!required.isAnnotationPresent(PieComponent.class))return;
        if(from!=null && !from.isAnnotationPresent(PieComponent.class))return;
        Object o = instanceMap.get(required.getName());
        if(o == null) {
            o = required.getConstructor().newInstance();
            instanceMap.put(required.getName(),o);
        }
        for(Field f : required.getDeclaredFields()) {
            if(f.isAnnotationPresent(Wire.class)) {
                String instance = f.getType().getName();
                if (from != null && instance.equalsIgnoreCase(from.getName()))
                    throw new IllegalArgumentException("Spigot Pie loading error:" +
                            "\nCircular reference: " +
                            "\n" + from.getName() + " -> " + required.getName() + " -> " + instance + "\n" +
                            "This type of Wiring is prohibited.");
                if(required.getName().equalsIgnoreCase(instance)) {
                    throw new IllegalArgumentException("Spigot Pie loading error:" +
                            "\nCircular reference: " +
                            "\n" + required.getName() + " -> " + instance + "\n" +
                            "This type of Wiring is prohibited.");
                }
                if(!instanceMap.containsKey(instance)) {
                    injectInstances(f.getType(),required);
                }
                f.setAccessible(true);
                f.set(o,instanceMap.get(instance));
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
                injectInstances(clazz,null);
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
