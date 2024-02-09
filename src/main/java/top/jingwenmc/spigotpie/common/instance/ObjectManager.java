package top.jingwenmc.spigotpie.common.instance;

import top.jingwenmc.spigotpie.common.SpigotPie;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ObjectManager {
    //All name is lower-case
    private static final Map<Class<?>,Map<String,Object>> typeMap = new ConcurrentHashMap<>();
    protected static void addObject(Class<?> type,String name,Object o) throws NameConflictException {
        name = name.toLowerCase();
        typeMap.computeIfAbsent(type, k -> new ConcurrentHashMap<>());
        Map<String,Object> map = typeMap.get(type);
        if(map.containsKey(name))throw new NameConflictException("Name Conflict. type:"+type+";name:"+name+";object:"+o);
        map.put(name,o);
        for(Class<?> c : type.getInterfaces()) {
            addObject(c,name,o);
        }
    }

    //null = not found
    public static Object getObject(Class<?> type,String name) {
        name = name.toLowerCase();
        if(typeMap.get(type) == null) {
            SpigotPie.getEnvironment().getLogger().log(Level.WARNING,"Object not found, please check your code. type:" + type + "; name:"+name);
            return null;
        }
        Map<String,Object> map = typeMap.get(type);
        if(map.size()==1) return map.values().stream().findFirst().get();
        if  (map.get(name) == null) {
            SpigotPie.getEnvironment().getLogger().log(Level.WARNING,"Object not found, please check your variable in code. type:" + type + "; variable name:"+name);
            return null;
        }
        return map.get(name);
    }

    public static <T> T getExactObject(Class<T> type,String name) {
        return type.cast(getObject(type,name));
    }

    public static boolean contains(Class<?> type) {
        return typeMap.containsKey(type);
    }
}
