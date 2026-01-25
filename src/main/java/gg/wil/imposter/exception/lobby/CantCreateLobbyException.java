package gg.wil.imposter.exception.lobby;

import gg.wil.imposter.exception.LobbyException;

public class CantCreateLobbyException extends LobbyException {
    public CantCreateLobbyException() {
        super("Couldn't create a lobby", LobbyExceptionType.CANT_CREATE_LOBBY, null, 409);
    }
}
