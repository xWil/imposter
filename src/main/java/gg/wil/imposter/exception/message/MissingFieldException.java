package gg.wil.imposter.exception.message;

import gg.wil.imposter.exception.MessageException;

public class MissingFieldException extends MessageException {

    public MissingFieldException(String message) {
        super(message, MessageExceptionType.MISSING_FIELD);
    }

    public MissingFieldException(String message, Throwable cause) {
        super(message, MessageExceptionType.MISSING_FIELD, cause);
    }
}
