package gg.wil.imposter.exception.websocket;

import gg.wil.imposter.exception.WebSocketException;

public class InvalidSessionIdException extends WebSocketException {

    public InvalidSessionIdException() {
        super("The given session ID is invalid.", WebSocketExceptionType.INVALID_SESSION_ID);
    }
}
