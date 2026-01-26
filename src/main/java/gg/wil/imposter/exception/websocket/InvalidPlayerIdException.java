package gg.wil.imposter.exception.websocket;

import gg.wil.imposter.exception.WebSocketException;

public class InvalidPlayerIdException extends WebSocketException {

    public InvalidPlayerIdException() {
        super("The given player ID is now allowed in that lobby", WebSocketExceptionType.INVALID_PLAYER_ID);
    }
}
