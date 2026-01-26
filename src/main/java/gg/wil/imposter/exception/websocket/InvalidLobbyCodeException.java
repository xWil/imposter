package gg.wil.imposter.exception.websocket;

import gg.wil.imposter.exception.WebSocketException;

public class InvalidLobbyCodeException extends WebSocketException {

    public InvalidLobbyCodeException() {
        super("Invalid lobby code provided", WebSocketExceptionType.INVALID_LOBBY_CODE);
    }
}
