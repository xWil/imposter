package gg.wil.imposter.exception;

public class MessageException extends RuntimeException {

    private final MessageExceptionType type;

    public MessageExceptionType getType() {
        return type;
    }

    public MessageException(String message, MessageExceptionType type) {
        super(message);
        this.type = type;
    }

    public MessageException(String message, MessageExceptionType type, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public enum MessageExceptionType {
        INVALID_DATA,
        INVALID_TYPE,
        MISSING_FIELD;
    }
}
