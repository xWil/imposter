package gg.wil.imposter.exception.websocket;

import gg.wil.imposter.exception.WebSocketException;

public class AlreadyConnectedException extends WebSocketException {

    public AlreadyConnectedException() {
        super("That session already has a connection.", WebSocketExceptionType.ALREADY_CONNECTED);
    }
}
