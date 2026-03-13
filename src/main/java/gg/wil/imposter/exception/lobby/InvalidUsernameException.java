package gg.wil.imposter.exception.lobby;

import gg.wil.imposter.exception.LobbyException;

public class InvalidUsernameException extends LobbyException {

    public InvalidUsernameException(String lobbyCode) {
        super("Username provided is invalid.", LobbyExceptionType.INVALID_USERNAME, lobbyCode, 409);
    }
}
