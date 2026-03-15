package gg.wil.imposter.exception.message;

import gg.wil.imposter.exception.MessageException;

public class InvalidDataException extends MessageException {

    public InvalidDataException(String message) {
        super(message, MessageExceptionType.INVALID_DATA);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, MessageExceptionType.INVALID_DATA, cause);
    }
}
