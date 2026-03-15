package gg.wil.imposter.exception.message;

import gg.wil.imposter.exception.MessageException;

public class InvalidTypeException extends MessageException {

    public InvalidTypeException(String message) {
        super(message, MessageExceptionType.INVALID_TYPE);
    }

    public InvalidTypeException(String message, Throwable cause) {
        super(message, MessageExceptionType.INVALID_TYPE, cause);
    }
}
