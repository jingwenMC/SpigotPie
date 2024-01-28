package top.jingwenmc.spigotpie.common.instance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectManager {
    //All name is lower-case
    private static final Map<Class<?>,Map<String,Object>> typeMap = new ConcurrentHashMap<>();
    protected static void addObject(Class<?> type,String name,Object o) throws NameConflictException {
        name = name.toLowerCase();
        if(typeMap.get(type)==null)typeMap.put(type,new ConcurrentHashMap<>());
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
        if(typeMap.get(type)==null)return null;
        Map<String,Object> map = typeMap.get(type);
        if(map.size()==1)return map.values().stream().findFirst().get();
        return map.get(name);
    }

    public static boolean contains(Class<?> type) {
        return typeMap.containsKey(type);
    }
}
