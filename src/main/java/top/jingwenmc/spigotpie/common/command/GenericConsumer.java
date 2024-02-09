package top.jingwenmc.spigotpie.common.command;

import lombok.SneakyThrows;
import top.jingwenmc.spigotpie.common.command.message.CommandMessageHandler;
import top.jingwenmc.spigotpie.common.command.message.MessageType;
import top.jingwenmc.spigotpie.common.instance.ObjectManager;
import top.jingwenmc.spigotpie.common.lang.PieLang;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class GenericConsumer implements Consumer<CommandItem> {
    private final Object targetObject;
    private final Method targetMethod;
    private final PieCommand pieCommand;
    private final List<Parameter> methodParameters;
    private final int leastRequired;

    public GenericConsumer(PieCommand pieCommand,Object o, Method m) throws TypeErrorException {
        targetObject = o;
        targetMethod = m;
        this.pieCommand = pieCommand;
        methodParameters = new ArrayList<>();
        boolean first = false;
        int leastRequired = 0;
        if(m.getParameterTypes().length == 1 && m.getParameterTypes()[0].getName().equalsIgnoreCase(SupportedTypes.COMMAND_ITEM)) {
            //Using raw CommandItem
            this.leastRequired = -1;
            return;
        }
        for(Parameter p : m.getParameters()){
            if(!first) {
                if(!Objects.equals(p.getType().getName(),SupportedTypes.COMMAND_SENDER)) {
                    throw new TypeErrorException("Unrecognized 1st parameter:"+p.getType().getName());
                }
                first = true;
                continue;
            }
            if(!Objects.equals(p.getType().getName(), SupportedTypes.STRING)) {
                throw new TypeErrorException("Unrecognized type:"+p.getType().getName());
            }
            if(!p.isAnnotationPresent(NotRequiredCommandParam.class)) {
                leastRequired++;
            }
            methodParameters.add(p);
        }
        this.leastRequired = leastRequired;
    }
    @SneakyThrows
    @Override
    public void accept(CommandItem commandItem) {
        CommandMessageHandler commandMessageHandler = ObjectManager.getExactObject(CommandMessageHandler.class,pieCommand.messageHandler());
        if(!((pieCommand.permission() == null || pieCommand.permission().isEmpty()) || commandItem.getSender().hasPermission(pieCommand.permission()))) {
            commandMessageHandler.handleMessage(commandItem,MessageType.NO_PERM,new ConcurrentHashMap<>());
            return;
        }
        if(leastRequired == -1) {
            targetMethod.invoke(targetObject,commandItem);
            return;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        boolean lock = false;
        StringJoiner stringJoiner = null;
        for(String s :commandItem.getArgs()) {
            if(!lock) {
                if (!s.startsWith("\"")) {
                    arrayList.add(s);
                } else {
                    if(s.endsWith("\"")) {
                        if(s.equalsIgnoreCase("\"")){
                            lock = true;
                            stringJoiner = new StringJoiner(" ");
                            stringJoiner.add("");
                        } else arrayList.add(s.substring(1,s.length()-1));
                    } else {
                        lock = true;
                        stringJoiner = new StringJoiner(" ");
                        stringJoiner.add(s.substring(1));
                    }
                }
            } else {
                if(s.endsWith("\"")) {
                    stringJoiner.add(s.substring(0,s.length()-1));
                    arrayList.add(stringJoiner.toString());
                    lock = false;
                } else {
                    stringJoiner.add(s);
                }
            }
        }
        String[] args = arrayList.toArray(new String[0]);
        if(lock)args = commandItem.getArgs();
        int length = args.length;
        if(length<leastRequired){
            Map<String,String> param = new ConcurrentHashMap<>();
            param.put("$1",String.valueOf(leastRequired));
            commandMessageHandler.handleMessage(commandItem, MessageType.TOO_FEW_ARGS,param);
            return;
        }
        if(length> methodParameters.size()){
            Map<String,String> param = new ConcurrentHashMap<>();
            param.put("$1",String.valueOf(methodParameters.size()));
            commandMessageHandler.handleMessage(commandItem, MessageType.TOO_MANY_ARGS,param);
            return;
        }
        List<Object> parameters = new ArrayList<>();
        parameters.add(commandItem.getSender());
        if(methodParameters.size() - leastRequired != 1) {
            parameters.addAll(Arrays.asList(args));
            for (int i = 0; i < methodParameters.size() - parameters.size(); i++) {
                parameters.add(methodParameters.get(parameters.size() + i).getAnnotation(NotRequiredCommandParam.class).value());
            }
        } else {
            int p = 0;
            for(Parameter pp : methodParameters) {
                if(pp.isAnnotationPresent(NotRequiredCommandParam.class) && length == leastRequired) {
                    parameters.add(pp.getAnnotation(NotRequiredCommandParam.class).value());
                } else {
                    parameters.add(args[p]);
                    p++;
                }
            }
        }
        try {
            targetMethod.invoke(targetObject, parameters.toArray());
        }catch (Exception e) {
            commandItem.getSender().sendMessage(PieLang.COMMAND_ERROR);
            e.printStackTrace();
        }
    }

    public static class SupportedTypes {
        public static final String COMMAND_SENDER = CommandSender.class.getName();
        public static final String STRING = String.class.getName();
        public static final String COMMAND_ITEM = CommandItem.class.getName();

        //future: add more support type & check type
    }
}
