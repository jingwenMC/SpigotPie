package top.jingwenmc.spigotpie.common.command;

public class TypeErrorException extends Exception{
    public TypeErrorException(String message) {
        super(message);
    }

    public TypeErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
